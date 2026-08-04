# 2026-08-04 로컬 성능 smoke 결과

## 용어 정리

- smoke test: 성능 한계를 찾기 전에 요청, 데이터, 메트릭, 대시보드가 연결되는지 낮은 부하로 확인하는 시험입니다.
- 측정 구간 (measure stage): Redis warm-up 같은 준비 요청을 빼고 p95·p99를 계산한 구간입니다.
- 누락 반복 (dropped iterations): k6가 예정한 시각에 시작하지 못한 요청 작업 수입니다.

## 핵심 답변

세 상품 조회 경로 모두 2 RPS를 30초 동안 처리했고, 기능 검사 실패와 누락 반복은 0건이었습니다. 측정 구간의 참고값은 Redis warm p95 18.24ms, denormalized p95 35.63ms, normalized p95 44.65ms였습니다.

중요: 각 경로의 측정 요청은 61건뿐입니다. p99 비교와 용량 판단에 필요한 표본 수가 아니며, 이번 결과는 전체 측정 경로가 동작한다는 증거입니다.

## 실행 환경

| 항목 | 값 |
| --- | --- |
| Git 기준 commit | `ea81bf3` + 작업 트리 변경 |
| 장비 | MacBook Air, Apple M4 10-core, 24GB |
| Java | OpenJDK 21.0.2 |
| Spring Boot | 3.4.4 |
| k6 | 2.1.0 |
| Docker client / server | 29.7.1 / 29.6.1 |
| Prometheus / Grafana | 3.5.0 / 12.1.0 |
| 실행 시간대 | Asia/Seoul |

## 데이터와 요청 조건

| 항목 | 값 |
| --- | --- |
| 브랜드 | 500건 |
| 상품 | 300,000건 |
| 회원 | 10,000건 |
| 좋아요 | 299,999건 |
| like_count 불일치 | 0건 |
| 브랜드 | `1,2,34` 고정 |
| 페이지 | `0,1` 순환 |
| 페이지 크기 | 20 |
| 정렬 | `LIKE_COUNT_DESC` |
| 목표 부하 | 2 RPS, 30초 |
| Redis | setup에서 0·1페이지 warm-up |

실행 순서는 `denormalized -> normalized -> redis`였습니다. 이 순서와 운영체제·DB 캐시 상태가 결과에 영향을 줄 수 있습니다.

## 측정 결과

k6 custom Trend인 `product_query_duration`만 사용했습니다. 따라서 setup 요청은 제외됩니다.

| variant | 측정 요청 | 실제 RPS | p50 | p95 | p99 | max | 오류 | dropped |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| normalized | 61 | 2.024 | 35.65ms | 44.65ms | 46.74ms | 47.05ms | 0% | 0 |
| denormalized | 61 | 2.014 | 30.71ms | 35.63ms | 37.64ms | 38.97ms | 0% | 0 |
| redis warm | 61 | 2.025 | 10.91ms | 18.24ms | 39.65ms | 54.53ms | 0% | 0 |

세 실행 모두 다음 예시 threshold를 통과했습니다.

- p95 < 500ms
- p99 < 1,000ms
- 기능 check 성공률 >= 99%
- 오류율 <= 1%
- dropped iterations = 0

이 threshold는 프로젝트의 운영 SLO가 아니라 실험 시작용 예시입니다.

## Prometheus·Grafana 검증

- Prometheus readiness: 성공
- `up{job="commerce-api"}`: `1`
- pg-simulator와 commerce-streamer: 이번 실험에서는 미실행이므로 `0`
- `k6_http_req_duration_p95{stage="measure"}`: 실제 시계열 확인
- 서버 `http_server_requests_seconds_bucket` 기반 p99 PromQL: 실제 값 계산 확인
- Grafana API에서 `Loopers Performance Lab` 대시보드 자동 등록 확인

setup 요청은 첫 DB 접근이나 Redis warm-up 때문에 느릴 수 있습니다. Grafana의 k6 RPS·오류·p95·p99 패널은 `stage="measure"`만 표시하도록 구성했습니다.

## 해석과 다음 실험

사실: 이 낮은 부하에서는 세 경로 모두 오류 없이 목표 RPS를 처리했습니다.

추론: Redis warm의 p50·p95가 낮은 것은 cache hit 효과와 일치하지만, 표본과 반복 횟수가 작아 성능 우위를 확정할 수 없습니다. Redis p99 한두 건은 p50보다 크게 흔들렸습니다.

다음 권장 실험:

1. 동일 조건을 최소 세 번 반복하고 실행 순서를 섞습니다.
2. 안정 구간마다 성공 요청 10,000건 이상을 확보합니다.
3. 20 -> 50 -> 100 -> 200 RPS로 높이며 p99, CPU, Tomcat busy, Hikari pending을 함께 봅니다.
4. Redis cold miss, warm hit, 동시 cold miss를 분리합니다.
5. 결과 내용 동일성과 DB·재고 같은 비즈니스 불변식을 별도 검증합니다.

원본 machine-readable summary:

- `k6/results/local-smoke-normalized.json`
- `k6/results/local-smoke-denormalized.json`
- `k6/results/local-smoke-redis.json`
