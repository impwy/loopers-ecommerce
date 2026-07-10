import { check, fail } from 'k6';
import http from 'k6/http';

const BASE_URL = (__ENV.BASE_URL || 'http://localhost:8080').replace(/\/$/, '');
const BRAND_IDS = __ENV.BRAND_IDS || Array.from({ length: 30 }, (_, index) => index + 1).join(',');
const PAGE = __ENV.PAGE || '0';
const SIZE = __ENV.SIZE || '20';
const SORT = __ENV.SORT || 'LIKE_COUNT_DESC';

const ENDPOINT_PATHS = {
  normalize: '/api/v1/products',
  denormalize: '/api/v1/products/denormalization',
  redis: '/api/v1/products/redis',
};

function positiveNumber(name, fallback) {
  const value = Number(__ENV[name] || fallback);
  if (!Number.isFinite(value) || value <= 0) {
    throw new Error(`${name} must be a positive number`);
  }
  return value;
}

export function createOptions(endpoint) {
  if (!ENDPOINT_PATHS[endpoint]) {
    throw new Error(`Unsupported endpoint: ${endpoint}`);
  }

  const warmupRate = positiveNumber('WARMUP_RATE', 5);
  const loadRate = positiveNumber('RATE', 20);
  const preAllocatedVUs = positiveNumber('PRE_ALLOCATED_VUS', 20);
  const maxVUs = positiveNumber('MAX_VUS', 100);
  const p90Milliseconds = positiveNumber('P90_MS', 300);
  const p95Milliseconds = positiveNumber('P95_MS', 500);
  const warmupDuration = __ENV.WARMUP_DURATION || '30s';

  return {
    discardResponseBodies: true,
    summaryTrendStats: ['avg', 'med', 'p(90)', 'p(95)', 'max'],
    systemTags: ['method', 'name', 'scenario'],
    tags: {
      endpoint,
      test_type: 'product-read',
    },
    scenarios: {
      warmup: {
        executor: 'constant-arrival-rate',
        rate: warmupRate,
        timeUnit: '1s',
        duration: warmupDuration,
        preAllocatedVUs,
        maxVUs,
      },
      load: {
        executor: 'constant-arrival-rate',
        startTime: warmupDuration,
        rate: loadRate,
        timeUnit: '1s',
        duration: __ENV.DURATION || '2m',
        preAllocatedVUs,
        maxVUs,
      },
    },
    thresholds: {
      'http_req_duration{scenario:load}': [
        `p(90)<${p90Milliseconds}`,
        `p(95)<${p95Milliseconds}`,
      ],
      'http_req_failed{scenario:load}': ['rate<0.01'],
      'checks{scenario:load}': ['rate>0.99'],
      'dropped_iterations{scenario:load}': ['count==0'],
    },
  };
}

function productListUrl(endpoint) {
  const path = ENDPOINT_PATHS[endpoint];
  const query = `sort=${SORT}&brandIds=${BRAND_IDS}&page=${PAGE}&size=${SIZE}`;

  return `${BASE_URL}${path}?${query}`;
}

function requestParams(endpoint, responseType = 'none') {
  const path = ENDPOINT_PATHS[endpoint];

  return {
    headers: {
      'X-USER-ID': 'loadtest',
    },
    responseType,
    tags: {
      name: `GET ${path}`,
      endpoint,
    },
  };
}

export function verifyProductData(endpoint) {
  const response = http.get(productListUrl(endpoint), requestParams(endpoint, 'text'));

  if (response.status !== 200) {
    fail(`Preflight failed for ${endpoint}: HTTP ${response.status}`);
  }

  let content;
  try {
    content = response.json().data.content;
  } catch (error) {
    fail(`Preflight failed for ${endpoint}: invalid JSON response`);
  }

  if (!Array.isArray(content) || content.length === 0) {
    fail(`Preflight failed for ${endpoint}: product data is empty`);
  }
}

export function requestProductList(endpoint) {
  const response = http.get(productListUrl(endpoint), requestParams(endpoint));

  check(response, {
    'status is 200': ({ status }) => status === 200,
  });
}
