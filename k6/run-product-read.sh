#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENDPOINT="${1:-}"

case "$ENDPOINT" in
  normalize | denormalize | redis)
    SCRIPT="$ROOT_DIR/k6/${ENDPOINT}_test.js"
    ;;
  *)
    echo "Usage: $0 {normalize|denormalize|redis}" >&2
    exit 2
    ;;
esac

command -v k6 >/dev/null 2>&1 || {
  echo "k6 is required: https://grafana.com/docs/k6/latest/set-up/install-k6/" >&2
  exit 1
}

PROMETHEUS_URL="${PROMETHEUS_URL:-http://localhost:9090}"
ACTUATOR_URL="${ACTUATOR_URL:-http://localhost:8081}"

curl -fsS "$PROMETHEUS_URL/-/ready" >/dev/null || {
  echo "Prometheus is not ready at $PROMETHEUS_URL" >&2
  exit 1
}

curl -fsS "$ACTUATOR_URL/actuator/health/readiness" >/dev/null || {
  echo "commerce-api is not ready at $ACTUATOR_URL" >&2
  exit 1
}

TEST_ID="${TEST_ID:-${ENDPOINT}-$(date +%Y%m%d-%H%M%S)}"
RESULT_DIR="$ROOT_DIR/k6/results"
mkdir -p "$RESULT_DIR"

export K6_PROMETHEUS_RW_SERVER_URL="${K6_PROMETHEUS_RW_SERVER_URL:-$PROMETHEUS_URL/api/v1/write}"
export K6_PROMETHEUS_RW_TREND_STATS="${K6_PROMETHEUS_RW_TREND_STATS:-p(90),p(95),avg,max}"

echo "endpoint=$ENDPOINT testid=$TEST_ID"
echo "Prometheus=$PROMETHEUS_URL Grafana=http://localhost:3000/d/loopers-performance"

k6 run \
  --out experimental-prometheus-rw \
  --tag "testid=$TEST_ID" \
  --tag "environment=${ENVIRONMENT:-local}" \
  --tag "endpoint=$ENDPOINT" \
  --tag "test_type=product-read" \
  --summary-export "$RESULT_DIR/$TEST_ID-summary.json" \
  "$SCRIPT"
