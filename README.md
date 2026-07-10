# Loopers Template (Spring + Java)
Loopers 에서 제공하는 스프링 자바 템플릿 프로젝트입니다.

## Getting Started
현재 프로젝트 안정성 및 유지보수성 등을 위해 아래와 같은 장치를 운용하고 있습니다. 이에 아래 명령어를 통해 프로젝트의 기반을 설치해주세요.

### Prerequisites

- Java 21
- Docker Desktop
- k6 2.1.0

Gradle daemon은 저장소의 `gradle/gradle-daemon-jvm.properties`에 따라 Java 21을 사용합니다.

```shell
./gradlew --version
```

### Environment
`local` 프로필로 동작할 수 있도록, 필요 인프라를 `docker-compose` 로 제공합니다.
```shell
docker compose -f ./docker/infra-compose.yml up -d
docker compose -f ./docker/infra-compose.yml ps -a
```

MySQL의 `loopers`, `paymentgateway` 데이터베이스와 Kafka의 기본 토픽은 초기화 서비스가 멱등하게 준비합니다.

### Applications

아래 애플리케이션은 각각 별도 터미널에서 실행합니다.

```shell
./gradlew :apps:pg-simulator:bootRun
./gradlew :apps:commerce-api:bootRun
./gradlew :apps:commerce-streamer:bootRun
```

| Application | API | Actuator |
| --- | --- | --- |
| commerce-api | http://localhost:8080 | http://localhost:8081 |
| pg-simulator | http://localhost:8082 | http://localhost:8083 |
| commerce-streamer | http://localhost:8084 | http://localhost:8085 |

```shell
curl -fsS http://localhost:8081/actuator/health
curl -fsS http://localhost:8083/actuator/health
curl -fsS http://localhost:8085/actuator/health
```

### Monitoring
`local` 환경에서 모니터링을 할 수 있도록, `docker-compose` 를 통해 `prometheus` 와 `grafana` 를 제공합니다.

애플리케이션 실행 이후, **http://localhost:3000** 로 접속해, admin/admin 계정으로 로그인하여 확인할 수 있습니다.
```shell
docker compose -f ./docker/monitoring-compose.yml up -d
```

- Kafka UI: http://localhost:9091
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- API Performance 대시보드: http://localhost:3000/d/loopers-performance

Prometheus는 `commerce-api`의 `/actuator/prometheus`를 5초 간격으로 수집합니다. 서버 응답시간 p90/p95는
Micrometer histogram으로 계산하며, Prometheus 시계열은 named volume에 7일간 보관합니다. Grafana 설정은
`grafana-data` volume을 삭제하기 전까지 유지됩니다.

### Performance Test

상품 목록 조회의 normalize, denormalize, Redis cache 방식을 동일한 입력으로 각각 측정합니다. 로컬 프로필은 애플리케이션
시작 시 스키마를 다시 생성하므로, `commerce-api`를 먼저 시작한 뒤 성능 테스트 데이터를 적재하고 재시작하지 않아야 합니다.
성능 측정 중 SQL 콘솔 출력이 응답시간을 왜곡하지 않도록 다음과 같이 실행합니다.

```shell
SPRING_JPA_SHOW_SQL=false ./gradlew :apps:commerce-api:bootRun
```

> `sql/data_setting.sql`은 로컬 성능 테스트 전용이며 `commerce-api`의 기존 비즈니스 데이터를 모두 삭제합니다.
> 공유 DB나 보존할 데이터가 있는 환경에서는 실행하면 안 됩니다.

```shell
docker compose -f ./docker/infra-compose.yml exec -T mysql \
  mysql -uapplication -papplication < ./sql/data_setting.sql

# 이전 Redis 조회 결과 제거
docker compose -f ./docker/infra-compose.yml exec redis-master redis-cli FLUSHDB
```

기본 부하는 30초 warm-up 후 20 RPS로 2분간 실행합니다. load 구간의 p90 300ms 미만, p95 500ms 미만,
HTTP 실패율 1% 미만, check 성공률 99% 초과, dropped iteration 0건을 자동 검증합니다.

```shell
./k6/run-product-read.sh normalize
./k6/run-product-read.sh denormalize
./k6/run-product-read.sh redis
```

각 실행 결과는 Prometheus로 전송되고 `k6/results/*-summary.json`에도 저장됩니다. 부하와 임계값은 환경변수로 조정할 수 있습니다.

```shell
RATE=50 DURATION=5m P90_MS=200 P95_MS=400 \
  ./k6/run-product-read.sh redis
```

Grafana의 k6 p90/p95는 네트워크를 포함한 클라이언트 관점이고, Spring p90/p95는 서버 내부 처리시간입니다. 두 값이 다른 것은
정상이며, 테스트 간 비교 시 같은 endpoint, 데이터, 부하, `testid` 조건을 사용해야 합니다.

## About Multi-Module Project
본 프로젝트는 멀티 모듈 프로젝트로 구성되어 있습니다. 각 모듈의 위계 및 역할을 분명히 하고, 아래와 같은 규칙을 적용합니다.

- apps : 각 모듈은 실행가능한 **SpringBootApplication** 을 의미합니다.
- modules : 특정 구현이나 도메인에 의존적이지 않고, reusable 한 configuration 을 원칙으로 합니다.
- supports : logging, monitoring 과 같이 부가적인 기능을 지원하는 add-on 모듈입니다.

```
Root
├── apps ( spring-applications )
│   ├── 📦 commerce-api
│   └── 📦 commerce-streamer
├── modules ( reusable-configurations )
│   ├── 📦 jpa
│   ├── 📦 redis
│   └── 📦 kafka
└── supports ( add-ons )
    ├── 📦 jackson
    ├── 📦 monitoring
    └── 📦 logging
```
