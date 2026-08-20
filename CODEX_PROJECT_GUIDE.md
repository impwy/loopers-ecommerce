# Codex 프로젝트 활용 가이드

이 문서는 현재 `loopers-ecommerce` 저장소에서 Codex로 프로젝트 분석, 기능 추가, 오류 수정을 진행할 때 필요한 스킬과 작업 방법을 정리한 가이드입니다.

## 프로젝트 요약

- Gradle Kotlin DSL 기반 멀티모듈 Spring Boot 프로젝트입니다.
- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2를 사용합니다.
- Gradle Wrapper 9.1.0과 Java 25 toolchain을 사용하며, `gradle/gradle-daemon-jvm.properties`가 Gradle 데몬 JVM 기준을 고정합니다.
- 주요 실행 앱은 `apps:commerce-api`, `apps:commerce-streamer`, `apps:pg-simulator`입니다.
- 공통 인프라 모듈은 `modules:jpa`, `modules:redis`, `modules:kafka`입니다.
- 부가 기능 모듈은 `supports:jackson`, `supports:logging`, `supports:monitoring`입니다.
- 로컬 인프라는 `docker/infra-compose.yml`의 MySQL, Redis master/replica, Kafka, Kafka UI를 사용합니다.
- 테스트는 JUnit 5, Spring Boot Test, Testcontainers(MySQL, Redis, Kafka)를 중심으로 구성되어 있습니다.

## 현재 프로젝트 구조

```text
apps/
  commerce-api/       # 커머스 API 서버: 상품, 브랜드, 회원, 포인트, 좋아요, 주문, 결제, 쿠폰, 재고, 랭킹
  commerce-streamer/  # Kafka consumer, 상품 메트릭/랭킹 집계, 배치
  pg-simulator/       # Kotlin 기반 결제 게이트웨이 시뮬레이터
modules/
  jpa/                # JPA, QueryDSL, Testcontainers MySQL 설정
  redis/              # Redis 설정 및 테스트 유틸
  kafka/              # Kafka producer/consumer 설정
supports/
  jackson/            # Jackson 설정
  logging/            # Logback/logging 설정
  monitoring/         # Actuator/monitoring 설정
docs/design/          # 요구사항, 시퀀스, 클래스 다이어그램, ERD
sql/                  # 성능/정규화 관련 SQL
k6/                   # 부하 테스트 스크립트
http/                 # API 수동 테스트 요청
```

## 사용할 만한 Codex 스킬

현재 저장소의 일반적인 백엔드 개발에는 별도 특수 스킬보다 Codex의 코드베이스 분석, Gradle 실행, 파일 편집 능력이 핵심입니다. 다만 상황별로 아래 스킬을 쓰면 좋습니다.

| 상황 | 추천 스킬 또는 방법 | 사용 목적 |
| --- | --- | --- |
| 저장소 구조 파악, 기능 위치 찾기 | 기본 Codex 코드 분석 | `rg`, Gradle 설정, 테스트, 설계 문서를 읽고 변경 범위를 좁힘 |
| GitHub PR/이슈 정리 | `github:github` | PR, 이슈, 변경 파일, 논의 내용 파악 |
| PR 리뷰 코멘트 반영 | `github:gh-address-comments` | 미해결 리뷰 스레드를 읽고 수정 범위 결정 |
| GitHub Actions 실패 수정 | `github:gh-fix-ci` | 실패한 CI 로그를 보고 재현 테스트와 수정 진행 |
| 로컬 변경 커밋/PR 생성 | `github:yeet` | 변경 범위 확인 후 브랜치, 커밋, draft PR 생성 |
| 반복 작업용 프로젝트 전용 스킬 만들기 | `skill-creator` | `loopers` 전용 분석/수정 규칙을 Codex 스킬로 고정 |

Vercel 관련 스킬은 이 저장소의 핵심 백엔드 작업에는 기본적으로 필요하지 않습니다. 프론트엔드, Vercel 배포, 웹 UI 검증 작업이 생길 때만 사용하면 됩니다.

## 추천하는 프로젝트 전용 Codex 스킬

반복적으로 이 저장소를 작업한다면 `loopers-spring-commerce` 같은 전용 스킬을 만들어 두는 것이 좋습니다. 스킬에는 아래 규칙을 넣으면 됩니다.

```md
# loopers-spring-commerce

Use this skill when working on the loopers-ecommerce Spring Boot multi-module commerce backend.

Before changing code:
- Check `git status --short`.
- Read `README.md`, `settings.gradle.kts`, relevant `build.gradle.kts`, and `docs/design/*` when the task touches requirements or architecture.
- Locate related controller, DTO, facade, application service, domain entity, repository port, repository adapter, and tests.

Architecture rules:
- Keep API code under `interfaces/api`.
- Put use-case orchestration in `application/*/*Facade`.
- Put query/modify application logic in `application/*/*QueryService` and `application/*/*ModifyService`.
- Keep outbound ports in `application/required`.
- Keep inbound service interfaces in `application/provided`.
- Keep JPA, Redis, Kafka adapters in `infrastructure`.
- Keep entity invariants and domain behavior in `domain`.
- Use `CoreException` and `ErrorType` for expected business failures.

Verification rules:
- Add or update domain tests for entity rules.
- Add or update integration tests for application/facade behavior.
- Add or update E2E API tests when request/response behavior changes.
- Prefer targeted Gradle tests first, then module tests if the risk is broad.
```

## 프로젝트 분석 방법

1. 현재 작업 상태를 확인합니다.

```shell
git status --short
```

2. 모듈과 의존성을 확인합니다.

```shell
sed -n '1,220p' settings.gradle.kts
sed -n '1,260p' build.gradle.kts
sed -n '1,220p' apps/commerce-api/build.gradle.kts
```

3. 요구사항과 설계를 확인합니다.

```shell
sed -n '1,260p' docs/design/01-requirements.md
sed -n '1,260p' docs/design/02-sequence-diagrams.md
sed -n '1,260p' docs/design/04-erd.md
```

4. 기능 키워드로 관련 코드를 찾습니다.

```shell
rg "ProductFacade|OrderFacade|PaymentFacade|CouponFacade" apps/commerce-api/src/main/java
rg "ProductFacadeIntegrationTest|OrderE2ETest|PaymentE2ETest" apps/commerce-api/src/test/java
```

5. 변경 범위를 레이어별로 매핑합니다.

```text
interfaces/api -> application facade -> application service -> domain -> application/required port -> infrastructure adapter -> test
```

## 기능 추가 절차

기능을 추가할 때는 아래 순서로 진행하는 것이 가장 안전합니다.

1. `docs/design/01-requirements.md`에서 요구사항을 확인하거나 새 요구사항을 명확히 적습니다.
2. 기존 API 컨트롤러와 DTO 패턴을 확인합니다.
3. 유스케이스 조합은 `Facade`에 둡니다.
4. 단일 책임의 조회/변경 로직은 `QueryService`, `ModifyService`에 둡니다.
5. 도메인 규칙은 엔티티나 값 객체 메서드로 이동합니다.
6. DB/Redis/Kafka 접근은 `application/required` 인터페이스와 `infrastructure` 구현체로 분리합니다.
7. 이벤트가 필요한 기능은 기존 outbox, event handler, Kafka producer/consumer 패턴을 먼저 확인합니다.
8. 캐시가 필요한 조회 기능은 Redis key, TTL, 무효화 조건을 먼저 정합니다.
9. 도메인 테스트, 통합 테스트, E2E 테스트 중 변경 위험에 맞는 테스트를 추가합니다.

예시 프롬프트:

```text
상품 목록 조회에 가격 범위 필터를 추가해줘.
기존 ProductV1ApiController, ProductFacade, ProductQueryService, ProductRepository 패턴을 따라가고,
QueryDSL/JPA 쿼리와 E2E 테스트까지 추가해줘.
변경 전 git status를 확인하고, 관련 테스트만 먼저 실행해줘.
```

## 오류 수정 절차

오류 수정은 재현과 회귀 테스트를 먼저 잡는 방식이 좋습니다.

1. 에러 메시지, 실패 테스트, 요청 URL, 로그 중 하나로 재현 조건을 정리합니다.
2. `rg`로 예외 메시지, API path, 메서드명, 도메인명을 검색합니다.
3. 실패 경로를 컨트롤러에서 도메인 또는 인프라까지 추적합니다.
4. 현재 기대 동작을 `docs/design/*`와 기존 테스트에서 확인합니다.
5. 가능하면 먼저 실패하는 회귀 테스트를 추가합니다.
6. 최소 범위로 수정합니다.
7. 수정한 레이어와 인접 레이어의 테스트를 실행합니다.

예시 프롬프트:

```text
주문 생성 시 재고가 부족한데도 결제가 진행되는 문제를 고쳐줘.
OrderFacade, InventoryFacade, PaymentFacade 흐름을 추적하고,
실패를 재현하는 통합 테스트를 먼저 추가한 뒤 수정해줘.
```

## 테스트 실행 가이드

전체 테스트는 Testcontainers 때문에 Docker가 필요할 수 있습니다.

특정 모듈 테스트:

```shell
./gradlew :apps:commerce-api:test
./gradlew :apps:commerce-streamer:test
./gradlew :apps:pg-simulator:test
```

특정 테스트 클래스:

```shell
./gradlew :apps:commerce-api:test --tests com.loopers.application.product.provided.ProductFacadeTest
./gradlew :apps:commerce-api:test --tests com.loopers.adapter.webapi.order.OrderE2ETest
./gradlew :apps:commerce-streamer:test --tests com.loopers.batch.job.WeeklyProductRankJobConfigTest
```

로컬 인프라 실행:

```shell
docker-compose -f ./docker/infra-compose.yml up
```

모니터링 실행:

```shell
docker-compose -f ./docker/monitoring-compose.yml up
```

앱 실행:

```shell
./gradlew :apps:commerce-api:bootRun
./gradlew :apps:commerce-streamer:bootRun
./gradlew :apps:pg-simulator:bootRun
```

## 변경 시 주의할 코드 패턴

- `ApiResponse.success(...)`와 `ApiResponse.fail(...)` 응답 형식을 유지합니다.
- 예상 가능한 비즈니스 실패는 `CoreException(ErrorType, message)`로 처리합니다.
- API 입력 오류는 `ApiControllerAdvice`에서 공통 처리하므로 개별 컨트롤러에 중복 예외 처리를 넣지 않습니다.
- JPA 엔티티는 기본 생성자를 `protected`로 두고 정적 팩토리 메서드를 사용하는 패턴이 많습니다.
- 테스트 데이터는 기존 Fixture 클래스를 우선 사용합니다.
- 통합 테스트는 `DatabaseCleanUp`, `RedisCleanUp`으로 상태를 정리하는 패턴을 따릅니다.
- 동시성, 재고, 결제, 쿠폰, 포인트는 트랜잭션 경계와 롤백 이벤트를 함께 확인해야 합니다.
- 상품 조회는 정규화 조회, 비정규화 조회, Redis 캐시 조회 경로가 함께 존재하므로 한 경로만 수정하지 않도록 주의합니다.
- 좋아요와 상품 랭킹은 이벤트, outbox, Kafka, Redis sorted set과 연결될 수 있습니다.
- `commerce-api`와 `commerce-streamer`가 같은 도메인 개념을 일부 공유하지만 패키지는 분리되어 있으므로 복사식 변경보다 각 앱의 책임을 먼저 확인합니다.

## Codex에게 요청할 때 좋은 형식

아래 정보가 포함될수록 분석과 수정 품질이 좋아집니다.

```text
목표:
- 어떤 기능을 추가하거나 어떤 오류를 고칠지

범위:
- commerce-api / commerce-streamer / pg-simulator 중 어디인지
- API, 도메인, 배치, Kafka, Redis, DB 중 어떤 영역인지

기대 동작:
- 정상 케이스와 실패 케이스

검증:
- 어떤 테스트를 추가하거나 실행해야 하는지
- 수동 API 요청이나 k6 검증이 필요한지

제약:
- 기존 API 응답 호환성 유지 여부
- DB 스키마 변경 가능 여부
- 캐시 키나 Kafka topic 호환성 유지 여부
```

## 추천 작업 단위

- 작은 기능: 컨트롤러, DTO, Facade, 서비스, 테스트까지 한 번에 요청해도 됩니다.
- 도메인 규칙 변경: 도메인 테스트를 먼저 만들고 application 통합 테스트를 추가하는 방식이 좋습니다.
- 결제/주문/재고/쿠폰: 한 번에 크게 바꾸기보다 실패 케이스별로 나누는 것이 안전합니다.
- Kafka/랭킹/캐시: producer, consumer, Redis key, batch job, 재처리 전략을 같이 확인해야 합니다.
- 성능 작업: `sql/`, `k6/`, QueryDSL/JPA 쿼리, Redis 캐시를 함께 확인해야 합니다.

## 빠른 체크리스트

기능 추가 전:

- [ ] `git status --short` 확인
- [ ] 요구사항과 설계 문서 확인
- [ ] 기존 유사 기능 검색
- [ ] 변경 레이어 범위 결정
- [ ] 테스트 전략 결정

구현 중:

- [ ] 기존 패키지 구조와 네이밍 유지
- [ ] 도메인 규칙은 도메인에 배치
- [ ] 인프라 접근은 repository port 뒤로 숨김
- [ ] 예외와 응답 형식 일관성 유지
- [ ] 캐시, 이벤트, 트랜잭션 영향 확인

완료 전:

- [ ] 관련 단위/통합/E2E 테스트 실행
- [ ] 실패 테스트가 있으면 원인 기록
- [ ] 불필요한 파일 변경 확인
- [ ] `.DS_Store` 같은 로컬 파일을 커밋 대상에서 제외
