# Loopers Codex Worker & Architecture Guide

이 문서는 이 저장소에서 작업하는 사람과 Codex가 함께 지켜야 하는 실행 계약이다. 기존 프로젝트 설명, Codex 작업 방법, GitHub 전달 절차, 헥사고날 아키텍처 규칙을 한곳에 통합한다.

## 1. 기본 원칙

- 사용자 요청과 GitHub Issue의 Acceptance Criteria를 작업 범위의 기준으로 삼는다.
- 중요한 가정은 비자명한 변경 전에 먼저 알린다.
- 여러 해석이 결과를 크게 바꾸면 질문하고, 그렇지 않으면 가장 작은 안전한 가정으로 진행한다.
- 요청 범위를 벗어난 리팩터링, 추상화, 설정 옵션을 추가하지 않는다.
- 기존 작업 트리의 변경은 사용자 소유다. 무관한 파일을 수정·삭제·스테이징하지 않는다.
- 버그 수정은 가능하면 실패를 재현하는 테스트를 먼저 추가한다.
- 완료 기준을 정하고 실제 명령으로 검증한 뒤 결과와 미검증 항목을 보고한다.

## 2. 저장소 개요

- Java 21, Gradle Kotlin DSL 기반의 Spring Boot 멀티모듈 프로젝트다.
- 실행 앱은 `apps:commerce-api`, `apps:commerce-streamer`, `apps:pg-simulator`다.
- `modules:jpa`, `modules:redis`, `modules:kafka`는 공유 기술 어댑터와 설정을 제공한다.
- `supports:*`는 Jackson, logging, monitoring 같은 횡단 기술 기능을 제공한다.
- 로컬 인프라는 `docker/infra-compose.yml`, 관측성 스택은 `docker/monitoring-compose.yml`을 사용한다.
- 테스트는 JUnit 5, Spring Boot Test, Testcontainers를 사용하므로 Docker가 필요할 수 있다.
- `CODEX_WORK_LOG.md`는 과거 분석 기록이다. 현재 규칙의 근거로 사용하지 말고, 별도 요청 없이 수정하지 않는다.

## 3. Issue-first 작업 계약

코드, 테스트, 설정, 문서 등 추적 파일을 바꾸는 요청은 구현 전에 GitHub Issue를 만든다. 순수 질의, 읽기 전용 분석, 상태 보고는 파일 변경이 없으면 Issue가 필요하지 않다.

1. `git status --short`와 현재 브랜치, 원격 기준 브랜치를 확인한다.
2. 기존 Issue에서 같은 목표가 진행 중인지 검색한다.
3. 없으면 `.github/ISSUE_TEMPLATE/work-item.yml` 형식으로 Issue를 만든다.
4. Issue에 문제, 범위, Acceptance Criteria, 테스트 계획, 호환성 위험을 적는다.
5. Issue 번호를 확보한 뒤에만 브랜치를 만들고 추적 파일을 수정한다.
6. 구현 중 범위가 커지면 현재 Issue를 비대하게 만들지 말고 별도 Issue로 분리한다.

GitHub 조회·Issue·PR 작업은 GitHub 연동 기능을 우선 사용한다. 연동 권한이 부족하면 인증된 `gh` CLI로 전환하고 그 사실을 보고한다. 브랜치 생성, 스테이징, 커밋, 푸시는 로컬 `git`을 사용한다.

## 4. 브랜치와 작업 트리

- 브랜치 이름은 `codex/issue-<number>-<kebab-case-slug>` 형식으로 만든다.
- 예: `codex/issue-31-codex-workflow`.
- 사용자가 base를 지정하지 않으면 현재 통합 브랜치와 열린 PR 구조를 확인해 base를 결정한다.
- 브랜치를 만들기 전에 `git fetch`로 local/remote base를 비교한다. local base가 뒤처졌으면 먼저 갱신하고, local base에 아직 push하지 않은 통합 커밋이 있으면 child PR에 섞이지 않도록 base 반영 여부를 먼저 명확히 한다.
- 기본적으로 Issue 하나당 브랜치 하나, PR 하나를 사용한다.
- 의도적인 stacked PR이 아니면 다른 미병합 기능 브랜치에서 새 브랜치를 만들지 않는다.
- mixed worktree에서는 `git add .` 또는 `git add -A`를 사용하지 않는다. 변경 범위의 경로만 명시적으로 스테이징한다.
- `.DS_Store`, IDE 설정, 로그, 빌드 결과, k6 결과 파일 같은 로컬 산출물을 커밋하지 않는다.
- 사용자의 기존 커밋을 재작성하거나 destructive Git 명령을 사용하지 않는다.

## 5. 커밋 규칙

Conventional Commit 형식을 사용한다.

```text
<type>(<scope>): <imperative summary>

Refs #<issue-number>
```

허용 type:

- `feat`: 사용자 또는 운영 기능 추가
- `fix`: 결함 수정
- `refactor`: 외부 동작을 바꾸지 않는 구조 개선
- `test`: 테스트만 변경
- `docs`: 문서만 변경
- `perf`: 성능 개선
- `chore`: 빌드, CI, 저장소 운영 변경

커밋은 하나의 검토 가능한 목적만 담는다. 제목은 짧고 명령형으로 쓰며, Issue 연결은 본문의 `Refs #N`으로 남긴다. 자동 생성 문구나 작성자 표시는 추가하지 않는다.

## 6. Pull Request와 Actions

- 원격 푸시 후 기본적으로 draft PR을 만든다.
- PR 제목도 Conventional Commit 형식을 사용한다.
- PR 본문 첫 부분에 `Refs #<issue-number>`를 넣는다. default branch를 직접 대상으로 하고 merge와 함께 Issue를 닫을 때만 `Closes #<issue-number>`를 사용한다.
- 변경 이유와 영향, 헥사고날 경계, API/DB/Kafka/Redis 호환성, 실행한 검증 명령을 기록한다.
- GitHub Actions가 실패하면 실패 로그를 확인하고 로컬에서 재현한 뒤 같은 Issue 범위에서 수정한다.
- CI 성공만으로 배포 준비가 보장되지는 않는다. 보안, 마이그레이션, 관측성, 롤백 조건도 별도로 확인한다.
- 비기본 브랜치 대상 PR의 `Closes #N`은 자동 종료를 보장하지 않는다. 해당 변경이 default branch에 도달하고 Acceptance Criteria가 충족됐는지 확인해 Issue를 직접 닫는다.

## 7. 헥사고날 아키텍처 기준

이 저장소의 각 실행 앱은 독립된 hexagon이다. 아래 첫 그림은 런타임 호출 순서다.

```text
REST / Kafka consumer / Scheduler / Batch launcher
                        |
                        v
           application.provided (input port)
                        |
                        v
          application use-case implementation
                  |           |
                  v           v
               domain   application.required
                                  |
                                  v
                    JPA / Redis / Kafka / HTTP
                       infrastructure adapter
```

컴파일 의존성은 다음처럼 안쪽의 계약을 향한다.

```text
interfaces ----> application.provided <---- application use case ----> domain
                                                   |
                                                   +----> application.required <---- infrastructure
```

Inbound adapter는 input port를 호출하고, use-case 구현은 input port를 구현하면서 domain과 output port 추상화에 의존하며, outbound adapter는 output port를 구현한다.

### `interfaces`

- REST controller, Kafka consumer, scheduler, batch launcher 같은 inbound adapter를 둔다.
- 전송 형식의 파싱, 검증, application-owned command/query/result로의 매핑만 담당한다.
- `application.provided`를 호출하며 repository나 infrastructure 구현을 직접 호출하지 않는다.
- API/Kafka DTO를 domain이나 application port의 타입으로 노출하지 않는다.

### `application.provided`

- 외부에서 호출할 use-case/input port 계약을 둔다.
- 시그니처에는 application 또는 domain이 소유한 타입만 사용한다.
- 새 facade/use case는 input port를 구현한다. controller, consumer, scheduler 같은 inbound adapter가 구체 facade를 직접 주입하는 기존 패턴은 새 코드에서 복제하지 않는다.

### `application/<feature>`

- use-case orchestration, transaction boundary, 권한·멱등성 흐름을 담당한다.
- domain과 `application.provided`/`application.required`에만 의존한다.
- REST/Kafka DTO, JPA repository, Redis 자료형, Feign client, 구체 producer를 import하지 않는다.
- 외부 시스템 실패를 application/domain 의미의 결과나 예외로 변환한다.
- 현재 프로젝트 관례의 `@Service`, `@Component`, `@Transactional`은 제한적인 framework 예외로 허용한다. 이 예외는 Web, persistence, messaging adapter 타입 의존을 허용하지 않는다.

### `application.required`

- 영속성, cache, message publication, clock, 외부 API를 위한 output port를 둔다.
- port는 구현 기술이 아니라 비즈니스가 필요로 하는 행위를 표현한다.
- 시그니처에 JPA projection, Redis tuple, Feign response, wire DTO를 노출하지 않는다.
- 새 port에는 Spring Data `Page`, `Pageable`, `Sort`를 노출하지 않고 application-owned paging/sort 타입으로 매핑한다. 현재 노출은 legacy로 취급한다.

### `domain`

- aggregate, entity, value object, policy, invariant, domain event를 둔다.
- `application`, `interfaces`, `infrastructure`를 import하지 않는다.
- repository, HTTP client, Redis/Kafka, Spring Web 타입에 의존하지 않는다.
- 현재 모델의 JPA mapping annotation과 `modules:jpa`의 `BaseEntity` 상속은 제한적인 legacy 예외다. 이 예외를 새로운 외부 기술 의존의 근거로 확대하지 않는다.

### `infrastructure`

- JPA/QueryDSL, Redis, Kafka producer, Feign/HTTP 같은 outbound adapter를 둔다.
- `application.required` port를 구현하고 외부 타입과 application/domain 타입 사이를 매핑한다.
- inbound REST DTO를 외부 API DTO로 재사용하지 않는다.

### `config`

- 명시적인 adapter 선택이나 여러 구현을 조립해야 하는 wiring을 두는 composition 경계다.
- 현재 component scanning과 stereotype 기반 조립은 제한적으로 유지할 수 있지만, adapter 선택 로직을 use case나 domain으로 이동하지 않는다.

### 앱과 공용 모듈

- 한 앱이 다른 앱의 구현 클래스를 import하지 않는다.
- `modules/*`와 `supports/*`에는 재사용 가능한 기술 기능만 둔다. 비즈니스 use case를 이동하지 않는다.
- 앱 사이에 공유할 비즈니스 개념이 생기면 contract 또는 shared-kernel 결정을 별도 Issue에서 명시한다.
- `commerce-streamer`의 `batch/*`는 batch 입력 경계를 가진 별도 하위 hexagon으로 취급한다.

## 8. 현재 구조에 대한 정직한 기준

현재 코드는 hexagonal package 구조를 사용하지만 일부 legacy 역방향 의존이 존재한다. 예를 들어 domain이 application/transport DTO를 참조하거나, application port가 infrastructure projection을 노출하는 코드가 있다.

- 기존 위반을 새 코드의 선례로 사용하지 않는다.
- 변경 범위 안에서 작은 비용으로 경계를 바로잡을 수 있으면 올바른 방향으로 이동한다.
- 여러 기능에 영향을 주는 migration은 현재 Issue에 끼워 넣지 않고 별도 architecture Issue를 만든다.
- 기존 위반이 남아 있는 상태에서 전역 ArchUnit 규칙을 즉시 활성화해 전체 CI를 깨뜨리지 않는다. 위반 목록과 단계적 허용 기준을 먼저 Issue로 설계한다.

## 9. 기능 변경 위치

일반적인 런타임 변경 흐름은 다음과 같다.

```text
interfaces adapter
  -> application.provided port
  -> application facade/service
  -> domain behavior
  -> application.required port
  -> infrastructure adapter
```

- 도메인 invariant는 entity/value object에 둔다.
- 여러 도메인 작업의 순서와 보상은 application use case에 둔다.
- DB/Redis/Kafka/외부 API 상세는 infrastructure에 둔다.
- application/interface의 오류 변환은 기존 `CoreException`/`ErrorType` 규칙과 응답 호환성을 따른다. `ErrorType`은 HTTP `HttpStatus`에 결합되어 있으므로 새 domain 규칙에는 domain-owned exception/error code를 사용하고 바깥 경계에서 변환한다.
- transaction, lock, retry, outbox, cache invalidation을 바꾸면 성공 경로뿐 아니라 중복·실패·롤백 경로도 검증한다.
- 상품 조회 변경은 normalize, denormalize, Redis 경로의 동작과 지표 호환성을 함께 확인한다.

## 10. 테스트 전략

- domain 규칙: 빠른 unit test
- application orchestration/transaction: integration test
- REST/Kafka inbound adapter: E2E 또는 adapter contract test
- JPA/Redis/Feign/Kafka outbound adapter: adapter integration test
- 동시성/lock/idempotency: 시작 조건을 통제한 반복 가능한 concurrency test
- schema/query/cache migration: 실제 Testcontainers 기반 integration test
- 성능 변경: 공통 부하 프로파일의 k6 p90/p95와 오류율 검증

변경 범위의 테스트를 먼저 실행한 뒤, 병합 전 전체 빌드를 실행한다.

```shell
./gradlew :apps:commerce-api:test
./gradlew :apps:commerce-streamer:test
./gradlew :apps:pg-simulator:test
./gradlew build --no-daemon --stacktrace --console=plain
```

테스트를 실행하지 못하면 성공으로 표현하지 않는다. 실패 원인, 필요한 외부 서비스, 시도한 명령을 보고한다.

## 11. 운영·보안 변경 기준

- secret, 실제 계정, token, 개인 데이터는 커밋하지 않는다.
- 로컬 기본 자격증명은 로컬 전용임을 문서화하고 운영 환경 변수와 분리한다.
- Actuator, Prometheus, Grafana, DB, Redis, Kafka 포트를 공개 인터넷에 그대로 노출하지 않는다.
- healthcheck는 각 서비스의 readiness를 확인하는 최소 하나의 책임으로 유지한다. dependency 대기는 Compose `depends_on`과 서비스별 healthcheck로 표현한다.
- API 변경은 인증/인가, 입력 검증, rate limit, timeout, CORS, 오류 노출을 확인한다.
- DB schema나 Kafka event 변경은 하위 호환성, rollout 순서, rollback을 PR에 적는다.
- 운영 배포는 CI 성공 외에도 환경 변수, migration, backup, alert, dashboard, rollback 절차를 확인해야 한다.

## 12. Definition of Done

- [ ] 구현 전에 연결된 GitHub Issue가 있다.
- [ ] 브랜치와 커밋이 Issue 번호를 추적한다.
- [ ] Acceptance Criteria를 모두 구현했다.
- [ ] 새 코드가 헥사고날 의존성 방향을 지킨다.
- [ ] 필요한 계층의 테스트를 추가하거나 갱신했다.
- [ ] 관련 테스트와 전체 빌드 결과를 기록했다.
- [ ] 비밀정보, 로컬 산출물, 무관한 사용자 변경이 포함되지 않았다.
- [ ] 문서와 운영 설정이 실제 동작과 일치한다.
- [ ] draft PR에 `Refs #N` 또는 default branch용 `Closes #N`, 영향, 호환성, 검증 결과가 있다.
- [ ] GitHub Actions 상태를 확인했다.
