import http from 'k6/http';

const BASE = 'http://localhost:8080';

export const options = {
  scenarios: {
    twenty_once: {
      executor: 'per-vu-iterations',
      vus: 50,
      iterations: 1,
      maxDuration: '1m',
    },
  },
};

function getRandomBrandIds(count = 30, max = 500) {
  const set = new Set();
  while (set.size < count) {
    const rand = Math.floor(Math.random() * max) + 1;
    set.add(rand);
  }
  return Array.from(set);
}

export default function () {
  const userId = `member${__VU}`;
  const sort = 'LIKE_COUNT_DESC';
  const page = 100;
  const brandIds = getRandomBrandIds();
  const params = {
    headers: {
      'X-USER-ID': userId,
    },
  };

  const url = `${BASE}/api/v1/products/redis?sort=${sort}&brandIds=${brandIds.join(',')}&page=${page}`;

  http.get(url, params);

}