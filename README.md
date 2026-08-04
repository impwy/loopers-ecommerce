# Loopers Template (Spring + Java)
Loopers 에서 제공하는 스프링 자바 템플릿 프로젝트입니다.

## Getting Started
현재 프로젝트 안정성 및 유지보수성 등을 위해 아래와 같은 장치를 운용하고 있습니다. 이에 아래 명령어를 통해 프로젝트의 기반을 설치해주세요.

### Prerequisites

- Java 21
- Docker Desktop

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

애플리케이션 실행 이후, **http://localhost:3000** 로 접속해, admin/admin 계정으로 로그인하여 확인하실 수 있습니다.
```shell
docker compose -f ./docker/monitoring-compose.yml up -d
```

- Kafka UI: http://localhost:9091
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

Grafana에는 `Loopers Performance Lab` 대시보드가 자동으로 등록됩니다. k6의 p95·p99와 Spring 서버의 HTTP 지연시간, JVM, Tomcat, HikariCP 지표를 함께 보는 실행 방법은 [성능 실험 가이드](docs/performance/README.md)를 참고하세요.

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
