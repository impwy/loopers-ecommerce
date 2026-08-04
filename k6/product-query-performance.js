import http from 'k6/http';
import { check, fail } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

const VARIANTS = {
  normalized: '/api/v1/products',
  denormalized: '/api/v1/products/denormalization',
  redis: '/api/v1/products/redis',
};

const PROFILE_DEFAULTS = {
  smoke: {
    executor: 'constant-arrival-rate',
    rps: 2,
    duration: '30s',
    preAllocatedVUs: 2,
    maxVUs: 10,
  },
  load: {
    executor: 'constant-arrival-rate',
    rps: 50,
    duration: '5m',
    preAllocatedVUs: 50,
    maxVUs: 200,
  },
  stress: {
    executor: 'ramping-arrival-rate',
    startRps: 25,
    rps: 200,
    duration: '3m',
    rampUpDuration: '2m',
    rampDownDuration: '1m',
    preAllocatedVUs: 100,
    maxVUs: 500,
  },
  spike: {
    executor: 'ramping-arrival-rate',
    startRps: 20,
    rps: 500,
    duration: '30s',
    baselineDuration: '1m',
    rampUpDuration: '5s',
    rampDownDuration: '30s',
    recoveryDuration: '1m',
    preAllocatedVUs: 100,
    maxVUs: 800,
  },
  soak: {
    executor: 'constant-arrival-rate',
    rps: 30,
    duration: '30m',
    preAllocatedVUs: 50,
    maxVUs: 200,
  },
};

function integerEnv(name, fallback, minimum = 1) {
  const raw = __ENV[name];
  if (raw === undefined || raw === '') {
    return fallback;
  }

  const value = Number(raw);
  if (!Number.isInteger(value) || value < minimum) {
    throw new Error(`${name} must be an integer greater than or equal to ${minimum}: ${raw}`);
  }
  return value;
}

function numberEnv(name, fallback, minimum, maximum) {
  const raw = __ENV[name];
  if (raw === undefined || raw === '') {
    return fallback;
  }

  const value = Number(raw);
  if (!Number.isFinite(value) || value < minimum || value > maximum) {
    throw new Error(`${name} must be between ${minimum} and ${maximum}: ${raw}`);
  }
  return value;
}

function integerListEnv(name, fallback, minimum) {
  const raw = __ENV[name] || fallback;
  const values = raw.split(',').map((item) => Number(item.trim()));
  if (values.length === 0 || values.some((value) => !Number.isInteger(value) || value < minimum)) {
    throw new Error(`${name} must be a comma-separated integer list (minimum ${minimum}): ${raw}`);
  }
  return values;
}

const VARIANT = (__ENV.VARIANT || 'normalized').toLowerCase();
const PROFILE = (__ENV.PROFILE || 'smoke').toLowerCase();

if (!Object.prototype.hasOwnProperty.call(VARIANTS, VARIANT)) {
  throw new Error(`VARIANT must be one of normalized, denormalized, redis: ${VARIANT}`);
}
if (!Object.prototype.hasOwnProperty.call(PROFILE_DEFAULTS, PROFILE)) {
  throw new Error(`PROFILE must be one of smoke, load, stress, spike, soak: ${PROFILE}`);
}

const BASE_URL = (__ENV.BASE_URL || 'http://localhost:8080').replace(/\/$/, '');
const ENDPOINT_PATH = VARIANTS[VARIANT];
const SORT = __ENV.SORT || 'LIKE_COUNT_DESC';
const SIZE = integerEnv('SIZE', 20);
const PAGES = integerListEnv('PAGE', '0,1', 0);
const BRAND_IDS = integerListEnv('BRAND_IDS', '1,2,34', 1);
const REDIS_WARMUP_PAGES = integerListEnv('REDIS_WARMUP_PAGES', '0,1', 0);
const USER_ID_PREFIX = __ENV.USER_ID_PREFIX || 'member';
const REQUEST_TIMEOUT = __ENV.REQUEST_TIMEOUT || '10s';
const TEST_ID = __ENV.TEST_ID || `${VARIANT}-${PROFILE}`;

if (VARIANT === 'redis' && PAGES.some((page) => page > 1)) {
  throw new Error(`Redis 목록 캐시는 PAGE 0 또는 1만 저장합니다: ${PAGES.join(',')}`);
}

const SLO_P95_MS = numberEnv('SLO_P95_MS', 500, 1, Number.MAX_SAFE_INTEGER);
const SLO_P99_MS = numberEnv('SLO_P99_MS', 1000, 1, Number.MAX_SAFE_INTEGER);
const SLO_ERROR_RATE = numberEnv('SLO_ERROR_RATE', 0.01, 0, 1);
const SLO_CHECK_RATE = numberEnv('SLO_CHECK_RATE', 0.99, 0, 1);
const SLO_DROPPED_ITERATIONS = integerEnv('SLO_DROPPED_ITERATIONS', 0, 0);

if (SLO_P99_MS < SLO_P95_MS) {
  throw new Error(`SLO_P99_MS (${SLO_P99_MS}) must be greater than or equal to SLO_P95_MS (${SLO_P95_MS})`);
}

const EXPERIMENT_TAGS = {
  variant: VARIANT,
  profile: PROFILE,
  testid: TEST_ID,
};
const MEASUREMENT_TAGS = {
  variant: VARIANT,
  profile: PROFILE,
  testid: TEST_ID,
  stage: 'measure',
};

const productQueryDuration = new Trend('product_query_duration', true);
const productQueryErrors = new Rate('product_query_errors');
const productQueryRequests = new Counter('product_query_requests');

function buildScenario() {
  const defaults = PROFILE_DEFAULTS[PROFILE];
  const rps = integerEnv('RPS', defaults.rps);
  const preAllocatedVUs = integerEnv('PRE_ALLOCATED_VUS', defaults.preAllocatedVUs);
  const maxVUs = integerEnv('MAX_VUS', defaults.maxVUs);

  if (maxVUs < preAllocatedVUs) {
    throw new Error(`MAX_VUS (${maxVUs}) must be greater than or equal to PRE_ALLOCATED_VUS (${preAllocatedVUs})`);
  }

  const common = {
    executor: defaults.executor,
    timeUnit: __ENV.TIME_UNIT || '1s',
    preAllocatedVUs,
    maxVUs,
    gracefulStop: __ENV.GRACEFUL_STOP || '30s',
    tags: EXPERIMENT_TAGS,
  };

  if (defaults.executor === 'constant-arrival-rate') {
    return {
      ...common,
      rate: rps,
      duration: __ENV.DURATION || defaults.duration,
    };
  }

  const startRate = integerEnv('START_RPS', defaults.startRps, 0);
  if (PROFILE === 'stress') {
    return {
      ...common,
      startRate,
      stages: [
        { duration: __ENV.RAMP_UP_DURATION || defaults.rampUpDuration, target: rps },
        { duration: __ENV.DURATION || defaults.duration, target: rps },
        { duration: __ENV.RAMP_DOWN_DURATION || defaults.rampDownDuration, target: 0 },
      ],
    };
  }

  return {
    ...common,
    startRate,
    stages: [
      { duration: __ENV.BASELINE_DURATION || defaults.baselineDuration, target: startRate },
      { duration: __ENV.RAMP_UP_DURATION || defaults.rampUpDuration, target: rps },
      { duration: __ENV.DURATION || defaults.duration, target: rps },
      { duration: __ENV.RAMP_DOWN_DURATION || defaults.rampDownDuration, target: startRate },
      { duration: __ENV.RECOVERY_DURATION || defaults.recoveryDuration, target: startRate },
    ],
  };
}

export const options = {
  scenarios: {
    product_query: buildScenario(),
  },
  tags: EXPERIMENT_TAGS,
  summaryTrendStats: ['count', 'avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
  thresholds: {
    product_query_duration: [`p(95)<${SLO_P95_MS}`, `p(99)<${SLO_P99_MS}`],
    product_query_errors: [`rate<=${SLO_ERROR_RATE}`],
    checks: [`rate>=${SLO_CHECK_RATE}`],
    dropped_iterations: [`count<=${SLO_DROPPED_ITERATIONS}`],
  },
};

function buildUrl(page) {
  const query = [
    `sort=${encodeURIComponent(SORT)}`,
    `brandIds=${BRAND_IDS.join(',')}`,
    `page=${page}`,
    `size=${SIZE}`,
  ].join('&');
  return `${BASE_URL}${ENDPOINT_PATH}?${query}`;
}

function requestParams(stage, userId) {
  return {
    headers: {
      Accept: 'application/json',
      'X-USER-ID': userId,
    },
    tags: {
      ...EXPERIMENT_TAGS,
      stage,
      name: 'product_query',
    },
    timeout: REQUEST_TIMEOUT,
  };
}

function parsePayload(response) {
  try {
    return response.json();
  } catch (_) {
    return null;
  }
}

function isProductPageResponse(response, payload) {
  return response.status === 200
    && payload
    && payload.meta
    && payload.meta.result === 'SUCCESS'
    && payload.data
    && Array.isArray(payload.data.content);
}

function assertEndpointAvailable(page) {
  const url = buildUrl(page);
  const response = http.get(url, requestParams('setup', `${USER_ID_PREFIX}setup`));
  const payload = parsePayload(response);

  if (!isProductPageResponse(response, payload)) {
    const detail = response.error
      || String(response.body || '').replace(/\s+/g, ' ').slice(0, 200)
      || 'empty response';
    fail(`[setup] 상품 조회 엔드포인트를 사용할 수 없습니다: ${url} (status=${response.status}, detail=${detail})`);
  }
}

export function setup() {
  // Redis 비교는 실제 측정 전에 캐시 대상인 0, 1페이지를 채운다.
  const setupPages = VARIANT === 'redis' ? REDIS_WARMUP_PAGES : [PAGES[0]];
  setupPages.forEach((page) => assertEndpointAvailable(page));
}

export default function () {
  const page = PAGES[__ITER % PAGES.length];
  const userId = __ENV.USER_ID || `${USER_ID_PREFIX}${__VU}`;
  const response = http.get(buildUrl(page), requestParams('measure', userId));
  const payload = parsePayload(response);

  const passed = check(response, {
    'HTTP status is 200': (result) => result.status === 200,
    'API result is SUCCESS': () => Boolean(payload && payload.meta && payload.meta.result === 'SUCCESS'),
    'response contains a product page': () => Boolean(payload && payload.data && Array.isArray(payload.data.content)),
  }, MEASUREMENT_TAGS);

  productQueryDuration.add(response.timings.duration, MEASUREMENT_TAGS);
  productQueryErrors.add(!passed, MEASUREMENT_TAGS);
  productQueryRequests.add(1, MEASUREMENT_TAGS);
}
