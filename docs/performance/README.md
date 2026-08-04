# Loopers 이커머스 성능 실험 가이드

## 용어 정리

- 백분위 지연시간 (percentile latency): 요청을 빠른 순서로 놓았을 때 일정 비율의 요청이 그 시간 안에 끝났음을 나타냅니다.
- 처리량 (throughput): 서버가 1초 동안 실제로 처리한 요청 수이며, 이 문서에서는 주로 RPS(Requests Per Second)로 표시합니다.
- 서비스 수준 목표 (Service Level Objective, SLO): 사용자가 받아야 할 성능과 안정성의 목표값입니다.
- 포화 (saturation): 스레드, DB 연결, CPU 같은 제한된 자원이 거의 다 사용되어 새 요청이 기다리는 상태입니다.
- 히스토그램 (histogram): 응답시간을 여러 구간에 나눠 요청 수를 저장하고, 서버 여러 대의 p95·p99도 합산해 계산할 수 있게 하는 메트릭입니다.

## 핵심 답변

이 저장소에서는 아래 흐름으로 성능 실험을 재현할 수 있습니다.

```text
k6 부하 발생기
  -> commerce-api:8080
     -> MySQL / Redis / Kafka / pg-simulator
  -> k6 결과 ---------> Prometheus:9090
commerce-api Actuator -> Prometheus:9090
Prometheus -----------> Grafana:3000
```

가정: 첫 실험 대상은 상품 목록 조회이며, 로컬 한 대에서 병목을 찾는 학습 실험입니다. 이 결과를 그대로 운영 용량으로 환산하지 않습니다.

현재 제공하는 항목은 다음과 같습니다.

- `normalized`, `denormalized`, `redis` 상품 조회를 같은 요청 패턴으로 비교하는 k6 실험
- smoke, load, stress, spike, soak 부하 프로필
- p50·p95·p99, RPS, 오류율, 목표 요청 누락, JVM, Tomcat, HikariCP를 한 화면에서 보는 Grafana 대시보드
- 브랜드 500건, 상품 30만 건, 회원 1만 건, 좋아요 약 30만 건의 로컬 데이터
- 대용량 트래픽에서 추가로 확인할 실험과 정확성 검증표

값의 구분:

- 사실: 소스 코드나 공식 문서로 확인한 내용입니다.
- 측정값: 이 환경에서 명령을 실행해 얻은 값이며, 실행 날짜와 조건을 함께 기록해야 합니다.
- 권장값: 첫 실험을 시작하기 위한 예시입니다. 실제 합격 기준은 서비스 SLO로 바꿔야 합니다.

## 1. 대상과 학습 목표

대상은 Spring Boot 서비스를 실행해 본 주니어~미들 백엔드 개발자입니다.

선행 조건:

- Java 21
- Docker Desktop
- k6
- 로컬 포트 `3000`, `8080`, `8081`, `9090` 사용 가능

이 실험을 끝내면 다음을 할 수 있어야 합니다.

1. 평균과 p95·p99의 차이를 설명합니다.
2. 목표 RPS를 유지하면서 지연시간이 무너지는 포화 지점을 찾습니다.
3. k6의 사용자 관점 지연과 Spring 서버 내부 지연을 구분합니다.
4. 느린 요청과 함께 CPU, Tomcat 스레드, HikariCP DB 연결 풀을 확인합니다.
5. 캐시 성능을 cold miss와 warm hit로 분리합니다.
6. 성능과 함께 재고·포인트·이벤트의 정합성을 검증합니다.

이번 구성에서 의도적으로 생략한 항목:

- 운영 환경의 분산 부하 발생기
- MySQL·Redis·Kafka 전용 exporter와 분산 추적
- 운영 SLO와 비용 기준
- 커널·네트워크 패킷 수준 분석

이 항목들은 8장의 후속 실험에서 다룹니다.

## 2. 10분 빠른 시작

### 2.1 기능 테스트 기준선 확인

목적은 부하 테스트 전에 기능 자체가 정상인지 확인하는 것입니다. 입력은 현재 소스 코드이고, 기대 결과는 `BUILD SUCCESSFUL`입니다.

```bash
mise exec java@21.0.2 -- ./gradlew build
```

전체 테스트는 Testcontainers로 MySQL과 Redis를 실행하므로 Docker Desktop이 켜져 있어야 합니다.

### 2.2 로컬 인프라 실행

```bash
docker compose -f docker/infra-compose.yml up -d
docker compose -f docker/infra-compose.yml ps -a
```

기대 신호는 MySQL, Redis, Kafka가 `healthy`이고 초기화 서비스가 성공 종료되는 것입니다.

### 2.3 commerce-api 실행

로컬 기본 설정은 SQL을 모두 출력하므로 성능 측정에서는 끕니다.

```bash
mise exec java@21.0.2 -- ./gradlew :apps:commerce-api:bootRun \
  --args='--spring.jpa.show-sql=false'
```

별도 터미널에서 확인합니다.

```bash
curl -fsS http://localhost:8081/actuator/health
```

기대 결과는 `status`가 `UP`인 응답입니다.

### 2.4 실험 데이터 적재

경고: `sql/data_setting.sql`은 실험 전용이며 지정한 네 테이블의 데이터를 삭제합니다. `local` 프로필이 새 스키마를 만든 직후에만 실행합니다.

```bash
docker compose -f docker/infra-compose.yml exec -T mysql \
  mysql -uapplication -papplication loopers < sql/data_setting.sql
```

적재 결과를 확인합니다.

```bash
docker compose -f docker/infra-compose.yml exec -T mysql \
  mysql -uapplication -papplication loopers \
  -e "SELECT COUNT(*) products FROM product; SELECT COUNT(*) likes FROM product_like;"
```

기대 규모는 상품 300,000건, 좋아요 299,999건입니다. 데이터 생성 중에는 부하 테스트를 시작하지 않습니다.

### 2.5 Prometheus와 Grafana 실행

```bash
docker compose -f docker/monitoring-compose.yml up -d
docker compose -f docker/monitoring-compose.yml ps
```

확인 위치:

- Prometheus targets: <http://localhost:9090/targets>
- Grafana: <http://localhost:3000> (`admin` / `admin`)
- 자동 설치 대시보드: `Dashboards > Loopers > Loopers Performance Lab`

Prometheus에서 다음 쿼리 결과가 `1`이면 commerce-api를 수집하고 있습니다.

```promql
up{job="commerce-api"}
```

### 2.6 첫 smoke 실험

목적은 경로·데이터·메트릭 연결을 짧게 검증하는 것입니다. 이 결과로 용량을 판단하지 않습니다.

```bash
mkdir -p k6/results

K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
K6_PROMETHEUS_RW_TREND_STATS=p(50),p(95),p(99),max \
k6 run -o experimental-prometheus-rw \
  -e PROFILE=smoke \
  -e VARIANT=denormalized \
  -e TEST_ID=local-smoke \
  --summary-export k6/results/local-smoke.json \
  k6/product-query-performance.js
```

합격 신호:

- HTTP 상태 검사 성공
- `http_req_failed` 기준 통과
- `dropped_iterations`가 0
- Grafana의 k6 및 commerce-api 패널에 같은 시간대 데이터가 표시됨

## 3. p95와 p99를 읽는 법

### 3.1 가장 작은 정확한 모델

요청 10,000개를 응답시간이 짧은 순서로 정렬했다고 가정합니다.

- p50: 가운데 요청의 응답시간에 가깝습니다.
- p95: 9,500개 요청은 이 값 이하, 약 500개는 이 값보다 느립니다.
- p99: 9,900개 요청은 이 값 이하, 약 100개는 이 값보다 느립니다.

비유: 평균 이동 시간은 하루 전체의 대략적인 교통 상황이고, p99는 혼잡한 순간에 실제로 겪을 수 있는 긴 이동 시간입니다. 비유의 한계는 p99가 단 하나의 최악값이 아니라 상위 1%의 경계라는 점입니다.

느린 요청 수를 대략 계산하는 식은 다음과 같습니다.

```text
경계보다 느린 요청/초 = RPS × (1 - P / 100)
```

- `RPS`: 초당 요청 수, 단위는 요청/초입니다.
- `P`: 백분위 숫자입니다. p99라면 `P = 99`입니다.

예를 들어 10,000 RPS에서 p99가 1초이면, 매초 약 100개 요청이 1초보다 느릴 수 있습니다.

### 3.2 평균만 보면 안 되는 이유

DB 잠금, 가비지 컬렉션, 커넥션 풀 대기처럼 일부 요청에만 생기는 지연은 평균에서 희석됩니다. 평균이 안정적이어도 p99와 타임아웃이 먼저 증가할 수 있습니다.

### 3.3 표본 수 주의

기존 k6 스크립트는 총 50건만 보냅니다. 50건의 p99는 사실상 가장 느린 한두 요청에 크게 흔들립니다.

권장: 엔드포인트와 안정 구간마다 성공 요청 10,000건 이상을 확보합니다. 이는 절대적인 통계 규칙이 아니라, 상위 1%에 약 100개 표본을 확보해 비교를 쉽게 하려는 실무 기준입니다. 같은 조건을 최소 세 번 반복하고 대표값과 변동 폭을 함께 봅니다.

## 4. 실험 프로필과 실행 명령

### 4.1 프로필 의미

| 프로필 | 질문 | 기본 형태 |
| --- | --- | --- |
| `smoke` | 경로와 측정 구성이 동작하는가? | 낮은 RPS, 짧은 실행 |
| `load` | 예상 부하를 지속해서 처리하는가? | 워밍업 - 유지 - 감소 |
| `stress` | 어느 RPS부터 무너지는가? | 여러 단계로 계속 증가 |
| `spike` | 갑작스러운 폭증 뒤 회복하는가? | 짧은 고부하 후 정상 부하 |
| `soak` | 오래 실행할 때 누수·지연 누적이 있는가? | 목표 부하 장시간 유지 |

k6는 도착률 기반 실행기를 사용합니다. 서버가 느려져도 예정한 요청률을 유지하려고 하므로 포화 지점을 보기 쉽습니다. 필요한 가상 사용자를 확보하지 못하면 `dropped_iterations`가 증가합니다.

### 4.2 조회 구현 비교

동일한 브랜드, 페이지, 정렬 조건으로 세 번 실행합니다.

```bash
for variant in normalized denormalized redis; do
  K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
  K6_PROMETHEUS_RW_TREND_STATS=p(50),p(95),p(99),max \
  k6 run -o experimental-prometheus-rw \
    -e PROFILE=load \
    -e VARIANT="$variant" \
    -e TEST_ID="query-$variant" \
    -e RPS=100 \
    -e BRAND_IDS=1,2,3 \
    -e PAGE=0 \
    -e SIZE=20 \
    k6/product-query-performance.js
done
```

비교표에는 다음을 기록합니다.

| 항목 | normalized | denormalized | redis warm |
| --- | ---: | ---: | ---: |
| 실제 RPS |  |  |  |
| p50 |  |  |  |
| p95 |  |  |  |
| p99 |  |  |  |
| 오류율 |  |  |  |
| dropped iterations |  |  |  |
| DB pool pending 최대 |  |  |  |
| CPU 최대 |  |  |  |

공정 비교 조건:

- 같은 데이터 스냅샷과 인덱스
- 같은 `BRAND_IDS`, `PAGE`, `SIZE`, `SORT`
- 같은 RPS와 실행 시간
- 워밍업 구간 제외
- SQL 로그 비활성화
- 한 실험이 끝난 뒤 자원이 정상으로 회복되었는지 확인

### 4.3 Redis cold와 warm 분리

사실: 목록 캐시는 `page <= 1`만 저장하며 TTL은 5분입니다. 기존 `redis_test.js`의 `page=100`과 매번 달라지는 브랜드 목록은 캐시 적중 실험이 아닙니다.

권장 순서:

1. Redis 캐시를 비운 뒤 첫 요청 한 번의 cold miss를 확인합니다.
2. 고정된 브랜드 순서와 `page=0`으로 warm hit를 반복합니다.
3. 캐시가 비어 있는 순간에 높은 동시 요청을 보내 cache stampede 여부를 봅니다.
4. `redis-cli MONITOR`는 부하 자체를 왜곡할 수 있으므로 짧은 진단에서만 사용합니다.

주의: 현재 캐시 키에는 `SIZE`가 없습니다. 같은 브랜드·정렬·페이지에 서로 다른 크기를 요청하면 잘못된 크기의 캐시 응답을 재사용할 수 있습니다. 성능보다 먼저 기능 정확성을 검증해야 합니다.

## 5. Grafana에서 보는 순서

### 5.1 사용자 관점

먼저 k6 패널을 봅니다.

1. 목표 RPS를 실제로 만들었는가?
2. `dropped_iterations`가 생겼는가?
3. 기능 검사와 HTTP 오류율이 기준을 넘었는가?
4. p95·p99가 어느 부하 단계부터 증가했는가?

### 5.2 서버 관점

그다음 같은 시간대의 commerce-api 패널을 봅니다.

1. URI별 서버 p95·p99
2. Tomcat busy threads / max threads
3. Hikari active / pending / max connections
4. process CPU
5. JVM heap, GC pause, live threads

판단 예:

| 증상 | 함께 보이는 신호 | 우선 의심 |
| --- | --- | --- |
| k6 p99만 높고 서버 p99는 낮음 | 연결·수신 시간이 증가 | 부하 발생기, 네트워크, 서버 앞 대기열 |
| 서버 p99와 Hikari pending 동시 증가 | active가 max에 근접 | 느린 쿼리, 긴 트랜잭션, DB 연결 부족 |
| p99와 Tomcat busy 동시 증가 | busy가 max에 근접 | 동기 작업, 외부 연동 지연, 워커 포화 |
| p99와 CPU 동시 증가 | DB pending은 낮음 | 계산, 직렬화, GC, 과도한 로깅 |
| p99가 급증하고 RPS가 감소 | dropped도 증가 | 시스템 또는 부하 발생기 포화 |
| 오류율만 증가하고 p99는 낮아짐 | 빠른 4xx·5xx | 실패 응답이 빨라 평균을 왜곡 |

성공과 오류 요청의 지연시간은 분리해서 봅니다. 실패가 빨리 반환되면 전체 p99가 좋아진 것처럼 보일 수 있습니다.

## 6. PromQL 예제

### 6.1 성공 요청 p95

```promql
histogram_quantile(
  0.95,
  sum by (le, uri) (
    rate(http_server_requests_seconds_bucket{
      application="commerce-api",
      status=~"2.."
    }[5m])
  )
)
```

p99는 `0.95`를 `0.99`로 바꿉니다. `histogram_quantile()` 전에 bucket을 합칠 때 `le` label을 반드시 보존해야 합니다.

### 6.2 URI별 RPS

```promql
sum by (uri) (
  rate(http_server_requests_seconds_count{
    application="commerce-api"
  }[1m])
)
```

### 6.3 5xx 비율

```promql
100 *
sum(rate(http_server_requests_seconds_count{
  application="commerce-api",
  status=~"5.."
}[1m]))
/
clamp_min(
  sum(rate(http_server_requests_seconds_count{
    application="commerce-api"
  }[1m])),
  0.001
)
```

### 6.4 SLO 경계 이내 비율

예를 들어 서버 응답시간 목표가 300ms라면 300ms bucket이 있어야 정확한 비율을 계산할 수 있습니다. 아래의 `le="0.3"` 시계열이 실제로 존재하는지 먼저 확인합니다.

```promql
sum(rate(http_server_requests_seconds_bucket{
  application="commerce-api",
  le="0.3"
}[5m]))
/
sum(rate(http_server_requests_seconds_count{
  application="commerce-api"
}[5m]))
```

## 7. 대용량 트래픽에서 측정할 것

### 7.1 네 가지 핵심 신호

| 범주 | 최소 지표 | 질문 |
| --- | --- | --- |
| 지연시간 | p50, p95, p99, timeout | 느린 사용자가 얼마나 많은가? |
| 트래픽 | 목표·실제 RPS, 동시 요청 | 필요한 부하를 처리했는가? |
| 오류 | HTTP 오류, 기능 check, retry | 빠르게 실패한 요청도 잡았는가? |
| 포화 | CPU, thread, DB pool, queue | 어떤 자원이 먼저 다 찼는가? |

### 7.2 계층별 체크리스트

| 계층 | 지표 | 경고 신호 |
| --- | --- | --- |
| k6 | actual RPS, p95/p99, failed, checks, dropped | 목표 RPS 미달, dropped 지속 증가 |
| HTTP | URI별 RPS·p95·p99·4xx·5xx | 일부 URI만 tail latency 증가 |
| Tomcat | busy/max threads, accept queue, rejected | busy가 max 근처에서 유지 |
| JVM | process CPU, heap, GC pause, thread | GC 뒤에도 heap이 회복되지 않음 |
| HikariCP | active/idle/pending/max, acquire timeout | pending 증가, active=max |
| MySQL | query p95/p99, rows examined, lock wait, deadlock, I/O | lock wait와 쿼리 지연 동시 증가 |
| Redis | hit/miss, command latency, memory, eviction, replica lag | hit율 저하, eviction·복제 지연 |
| Kafka | produce/consume rate, retry, consumer lag, E2E lag | lag가 부하 종료 뒤에도 감소하지 않음 |
| 외부 PG | p95/p99, timeout, circuit state | 외부 지연이 DB pool까지 점유 |
| 비즈니스 | 재고·잔액·중복 주문·이벤트 유실 | HTTP 성공 수와 최종 상태 불일치 |

메트릭 label에는 회원 ID, 주문 ID처럼 값이 계속 늘어나는 식별자를 넣지 않습니다. 시계열 수가 폭증해 Prometheus 자체가 병목이 될 수 있습니다.

## 8. 프로젝트 전용 후속 실험

### 실험 A. 정규화 대 비정규화 대 캐시

- 시나리오: 브랜드별 상품을 좋아요 순으로 조회
- 가설: 조인·집계보다 비정규화가 빠르고, warm cache가 가장 빠르다.
- 확인: p95/p99, rows examined, CPU, Hikari pending, 응답 내용 동일성
- 실패 진단: `EXPLAIN ANALYZE`, 인덱스 전후 비교, cache hit 분리

주의: 현재 정규화 경로는 좋아요 수를 집계하지만 정렬 기준은 집계값이 아닌 `product.like_count`입니다. 성능 비교 전에 두 경로가 같은 의미의 결과를 만드는지 확인합니다.

### 실험 B. Hot SKU 재고 경합

- 시나리오: 같은 상품 한 개에 주문을 집중
- 가설: 비관적 잠금이 초과 판매는 막지만 lock wait 때문에 p99가 증가한다.
- 확인: 성공 주문 수, 남은 재고, p95/p99, lock wait, deadlock, Hikari pending
- 불변식: `초기 재고 = 성공적으로 반영된 주문 수 + 최종 재고`

여러 상품을 한 주문에서 서로 다른 순서로 잠그면 deadlock 가능성도 별도로 확인합니다.

### 실험 C. 포인트 동시 충전

- 시나리오: 한 회원에게 동시에 포인트를 충전
- 가설: 잠금이나 버전 검사가 없으면 lost update가 발생할 수 있다.
- 확인: HTTP 성공 수뿐 아니라 최종 잔액
- 불변식: `최종 잔액 = 초기 잔액 + 성공 수 × 충전액`

### 실험 D. PG 지연과 회로 차단기

- 시나리오: 100~500ms 지연과 실패가 있는 pg-simulator 호출
- 가설: DB 트랜잭션 안의 동기 외부 호출이 Hikari 연결을 오래 점유한다.
- 확인: 결제 p99, Hikari active/pending, 회로 차단기 상태, 최종 결제 상태

HTTP 200만 성공으로 세면 안 됩니다. fallback이 외부 실패를 HTTP 성공처럼 보이게 할 수 있으므로 DB의 결제 상태까지 확인합니다.

### 실험 E. 주문에서 Kafka 소비까지

- 시나리오: 주문 이벤트를 생성하고 commerce-streamer가 집계할 때까지 측정
- 확인: HTTP p99와 별도로 producer 오류, outbox 최고 대기 시간, consumer lag, 이벤트 end-to-end 지연
- 정확성: 생성 이벤트 수, broker 수신 수, 처리 완료 수가 일치하는지 확인

현재 코드에는 선행 정확성 점검이 필요합니다.

- streamer의 상품 메트릭 카운터가 첫 판매 이벤트에서 null일 가능성
- producer 전송 완료를 기다리지 않고 outbox를 완료 처리하는 경로
- 주문 재고 처리와 쿠폰 처리의 서로 다른 트랜잭션 시점

이 문제를 확인하기 전에는 Kafka 처리량 수치를 성공 결과로 해석하지 않습니다.

## 9. 실패 진단 순서

### 증상 1. p99만 갑자기 높아짐

1. 오류 요청을 제외한 p99인지 확인합니다.
2. k6와 서버 p99를 비교합니다.
3. Tomcat busy, Hikari pending, CPU, GC를 같은 시간축에서 확인합니다.
4. DB slow query와 lock wait를 확인합니다.
5. 최근 배포·캐시 만료·부하 단계 변경 시점을 겹쳐 봅니다.

### 증상 2. 목표 RPS를 만들지 못함

1. `dropped_iterations`를 확인합니다.
2. 부하 발생기 CPU·메모리가 충분한지 확인합니다.
3. `preAllocatedVUs`와 `maxVUs`가 너무 작은지 확인합니다.
4. 서버의 빠른 실패나 연결 거부를 확인합니다.

### 증상 3. Redis가 DB보다 느림

1. 실제 cache hit인지 확인합니다.
2. cache key가 요청마다 달라지는지 확인합니다.
3. 값 직렬화·응답 크기를 비교합니다.
4. replica lag로 write 직후 miss가 나는지 확인합니다.
5. cold와 warm 결과를 섞지 않았는지 확인합니다.

## 10. 실험 결과 기록 양식

각 실행마다 아래 내용을 남깁니다.

```text
실험 ID:
Git commit:
실행 시각/시간대:
장비 CPU·메모리:
Java / k6 / Docker 버전:
데이터 건수와 인덱스:
프로필 / variant / 목표 RPS / 실행 시간:
워밍업 제외 구간:

실제 RPS:
p50 / p95 / p99:
오류율 / check 실패 / dropped iterations:
CPU / Tomcat busy / Hikari active·pending:
DB lock·slow query / Redis hit·miss / Kafka lag:
비즈니스 불변식 결과:

관찰된 병목:
변경한 한 가지:
재실행 결과와 변동 폭:
결론 또는 다음 실험:
```

한 번에 한 변수만 바꿉니다. 예를 들어 인덱스와 Hikari pool 크기를 동시에 바꾸면 어떤 변경이 효과를 냈는지 알 수 없습니다.

## 11. 연습 문제

1. 평균 80ms, p95 200ms, p99 2s라면 어떤 사용자가 문제를 겪고 있습니까?
2. p99와 Hikari pending이 함께 증가하지만 CPU는 낮습니다. 첫 진단 대상은 무엇입니까?
3. Redis warm 실험에서 요청마다 브랜드 순서를 바꾸면 어떤 일이 생깁니까?
4. HTTP 오류율이 0%여도 주문 성능 실험이 실패할 수 있는 이유는 무엇입니까?
5. 목표가 500 RPS인데 실제 420 RPS이고 dropped iterations가 증가합니다. 무엇을 먼저 구분해야 합니까?

### 정답과 판단 근거

1. 상위 약 1%의 요청이 최대 수초 지연을 겪는 꼬리 지연 문제입니다. 평균만으로는 숨겨집니다.
2. DB 연결 획득 대기, 느린 쿼리, 긴 트랜잭션, DB 잠금을 먼저 확인합니다.
3. 현재 캐시 키에 브랜드 순서가 들어가므로 논리적으로 같은 조건도 다른 key가 되어 miss와 key 파편화가 늘어납니다.
4. 초과 판매, lost update, 중복 처리처럼 HTTP 200 뒤의 최종 상태가 틀릴 수 있기 때문입니다.
5. 서버 용량 부족과 부하 발생기 VU·CPU 부족을 먼저 구분합니다. k6와 서버 자원 신호를 함께 봅니다.

전이 질문: 상품 조회가 빨라졌지만 DB CPU가 더 높아졌다면 이 변경을 배포해야 할까요? 사용자 SLO, 최대 부하, 비용, 다른 쿼리에 미치는 영향을 함께 비교해야 합니다.

## 12. 공식 참고 자료

- [Prometheus - Histograms and summaries](https://prometheus.io/docs/practices/histograms/)
- [Prometheus - histogram_quantile](https://prometheus.io/docs/prometheus/latest/querying/functions/#histogram_quantile)
- [Prometheus - Instrumentation practices](https://prometheus.io/docs/practices/instrumentation/)
- [Micrometer - Histograms and percentiles](https://docs.micrometer.io/micrometer/reference/concepts/histogram-quantiles.html)
- [Spring Boot 3.4 - Metrics](https://docs.spring.io/spring-boot/3.4/reference/actuator/metrics.html)
- [Grafana k6 - Thresholds](https://grafana.com/docs/k6/latest/using-k6/thresholds/)
- [Grafana k6 - Open and closed models](https://grafana.com/docs/k6/latest/using-k6/scenarios/concepts/open-vs-closed/)
- [Grafana k6 - Prometheus remote write](https://grafana.com/docs/k6/latest/results-output/real-time/prometheus-remote-write/)
- [Google SRE - Monitoring distributed systems](https://sre.google/sre-book/monitoring-distributed-systems/)
- [Google SRE - Service level objectives](https://sre.google/sre-book/service-level-objectives/)

문서의 숫자 예시는 합성 예시입니다. 실제 프로젝트 측정값은 실행 조건과 함께 별도 결과 기록에 남깁니다.
