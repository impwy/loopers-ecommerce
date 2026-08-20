# Codex 작업 기록

이 문서는 `loopers-ecommerce` 프로젝트를 분석하면서 추가한 테스트, 수정한 코드, 발견한 문제, 검증 결과를 챕터별로 기록합니다.

## 분석 기준

- 브랜치: `base-week10`
- 최근 이력: `round-9 ranking api` 이후 `base-week10`에서 일간/주간/월간 랭킹 API와 배치 작업이 집중적으로 추가됨
- 우선순위: 기존 요구사항과 최근 커밋 흐름 기준으로 데이터 정합성, 랭킹 조회 정확성, 배치 실행 경로, 테스트 공백을 먼저 확인

## Chapter 1. 상품 좋아요 이벤트와 도메인 불변식

### 확인한 문제

- `ProductFacade.decreaseLikeCount()`가 좋아요 감소 outbox를 만들면서 `PRODUCT_LIKE_INCREMENT` 타입을 사용하고 있었습니다.
- `Product.decreaseLikeCount()`가 현재 좋아요 수를 확인하지 않아 0에서 호출되면 `likeCount`가 음수가 될 수 있었습니다.
- 기존 테스트는 상품 생성만 확인했고, 좋아요 수 감소 불변식과 감소 이벤트 타입을 검증하지 않았습니다.

### 수정한 내용

- 좋아요 감소 outbox event type을 `PRODUCT_LIKE_DECREMENT`로 수정했습니다.
- `Product.decreaseLikeCount()`에 좋아요 수가 0 이하일 때 실패하는 가드를 추가했습니다.
- 도메인 테스트에 좋아요 감소 성공/실패 케이스를 추가했습니다.
- `ProductOutboxTest`를 추가해 좋아요 증가/감소 시 outbox가 각각 증가/감소 이벤트 타입으로 생성되고 `LikeIncrease`/`LikeDecrease` 이벤트가 발행되는지 검증했습니다.

### 필요한 테스트

- `ProductTest`: 좋아요 수가 음수가 되지 않는 도메인 불변식
- `ProductOutboxTest`: 좋아요 증가/감소 outbox/event 타입 정합성

### 추가로 얻은 지식과 확인할 점

- 좋아요 증감은 단순 카운터 변경이 아니라 outbox, async event handler, Kafka producer, streamer 메트릭까지 연결됩니다.
- 이후 추가 확인 대상으로는 좋아요 취소 API가 상품 like count 감소까지 호출하는지, Redis 캐시와 DB 카운트가 함께 갱신되는지 확인할 필요가 있습니다.

## Chapter 2. 상품 목록 브랜드 필터와 페이지 메타데이터

### 확인한 문제

- `ProductQueryDslRepositoryImpl.findByBrandAndLikeCount()`의 content 쿼리는 브랜드 필터를 적용하지만, total count 쿼리는 브랜드 필터를 적용하지 않았습니다.
- API 응답의 `totalElements`가 선택한 브랜드의 상품 수가 아니라 전체 상품 수로 계산될 수 있었습니다.
- 이 문제는 페이지 수, 다음 페이지 여부, 클라이언트 목록 UI에 직접 영향을 줍니다.

### 수정한 내용

- `findByBrandAndLikeCount()` count 쿼리에 `product.brand.id.in(brandIds)` 조건을 추가했습니다.
- `ProductFinderTest`에 브랜드 필터가 content와 total count에 함께 반영되는 통합 테스트를 추가했습니다.

### 필요한 테스트

- 여러 브랜드의 상품이 함께 있을 때 특정 브랜드로 조회하면 content와 `totalElements`가 모두 필터된 결과만 반영되는지 확인하는 테스트가 필요합니다.

### 검증 결과

- `mise exec java@21.0.2 -- ./gradlew :apps:commerce-api:compileTestJava` 성공.
- `ProductFinderTest` 실행은 Testcontainers가 Docker Desktop에 Docker API 1.32로 접근하면서 현재 Docker의 최소 API 1.40 요구와 충돌해 실패했습니다.
- `DOCKER_API_VERSION=1.40`, `TESTCONTAINERS_RYUK_DISABLED=true`, `DOCKER_HOST=unix:///Users/yong/.docker/run/docker.sock` 조합을 각각 확인했지만 로컬 Testcontainers Docker 환경 탐색 문제는 해소되지 않았습니다.

### 추가로 얻은 지식과 확인할 점

- 이 프로젝트에는 정규화 조회와 비정규화 조회가 공존합니다. 비정규화 조회 쿼리는 이미 total count에 브랜드 필터를 적용하고 있었고, 정규화 조회 경로만 누락되어 있었습니다.
- 향후 상품 목록 필터가 추가되면 content 쿼리와 count 쿼리에 같은 조건이 적용되는지 반드시 함께 확인해야 합니다.

## Chapter 3. 랭킹 API 날짜, 페이지, Redis 값 타입

### 확인한 문제

- `RankFacade`가 `pageable.withPage(page)`의 반환값을 사용하지 않아 요청한 `page`가 무시되고 항상 0페이지로 조회될 수 있었습니다.
- 일간 랭킹 조회는 API 요청의 `date`를 받지만 `RankFinder.getDailyRanking()` 시그니처에 날짜가 없어 실제 조회에 사용할 수 없었습니다.
- 일간 랭킹 Redis key가 streamer에서 저장하는 `ranking:all:yyyyMMdd`와 API 조회의 `ranking:daily:yyyyMMdd`로 불일치했습니다.
- 주간 랭킹은 `date.now()`를 사용해 요청 날짜가 아니라 현재 날짜 기준 주간 key를 조회했습니다.
- streamer는 Redis ZSET productId 값을 문자열로 저장하는데, API는 `Long.class::cast`로 변환해 `ClassCastException`이 발생할 수 있었습니다.

### 수정한 내용

- `RankFacade`에서 `PageRequest.of(page, size)`를 사용하도록 수정했습니다.
- `RankFinder.getDailyRanking()`에 `LocalDate date`를 추가하고 facade에서 요청 날짜를 넘기도록 수정했습니다.
- `RankQueryService` 일간 key를 `ranking:all:yyyyMMdd`로 통일했습니다.
- 주간 key 계산을 요청 날짜 기준으로 수정했습니다.
- Redis ZSET 값이 `Number` 또는 `String`이어도 Long productId로 변환되도록 파싱 로직을 추가했습니다.
- Redis 조회 결과가 `null`이어도 빈 랭킹으로 처리하도록 보강했습니다.

### 필요한 테스트

- `RankFacadeTest`: 요청 page/size가 `Pageable`에 반영되는지 확인.
- `RankFinderTest`: 일간 랭킹이 요청 날짜 key를 조회하고 문자열 productId를 변환하는지 확인.
- `RankFinderTest`: 주간 랭킹이 현재 날짜가 아니라 요청 날짜가 속한 주의 key를 조회하는지 확인.

### 검증 결과

- `mise exec java@21.0.2 -- ./gradlew :apps:commerce-api:test --tests com.loopers.application.rank.RankFacadeTest --tests com.loopers.application.rank.RankQueryServiceTest` 성공.

### 추가로 얻은 지식과 확인할 점

- API와 streamer가 Redis key 규칙을 공유하지 않으면 기능이 정상 동작해도 조회 결과가 비어 보일 수 있습니다.
- 랭킹 결과 순서는 Redis ZSET 순서가 기준이므로, DB `IN` 조회 후 순서 보존 여부는 별도 테스트와 개선이 필요합니다.

## Chapter 4. 월간 랭킹 배치의 Redis 적재 Step 연결

### 확인한 문제

- `MonthlyInMemoryTaskLet`과 `monthlyRankInMemoryStep()`은 구현되어 있었지만 `monthlyProductRankJob()`이 해당 step을 실행하지 않았습니다.
- 결과적으로 월간 랭킹 배치는 DB 집계만 수행하고, API가 조회하는 Redis 월간 랭킹 key는 갱신되지 않을 수 있었습니다.
- 주간 배치 job은 DB 집계 step 뒤에 in-memory step을 연결하고 있어 월간 배치만 패턴이 어긋나 있었습니다.

### 수정한 내용

- `monthlyProductRankJob()`에 `.next(monthlyRankInMemoryStep())`를 추가했습니다.
- Spring context와 Testcontainers 없이 job 구성만 확인하는 `MonthlyProductRankJobConfigTest`를 추가했습니다.

### 필요한 테스트

- 월간 랭킹 job이 `monthlyRankStep` 다음 `monthlyInMemoryStep`을 포함하는지 확인하는 구성 테스트가 필요합니다.
- 별도 환경에서는 실제 배치 실행 후 Redis `ranking:monthly:{year}_{month}` key가 생성되는 통합 테스트도 추가할 수 있습니다.

### 검증 결과

- `mise exec java@21.0.2 -- ./gradlew :apps:commerce-streamer:test --tests com.loopers.batch.job.MonthlyProductRankJobConfigTest` 성공.

### 추가로 얻은 지식과 확인할 점

- 배치 job은 step 메서드가 존재하는 것만으로 실행되지 않으며, `JobBuilder` 흐름에 명시적으로 연결되어야 합니다.
- 주간/월간 배치는 날짜 계산, DB 저장, Redis 적재가 같은 패턴이어야 하므로 한쪽만 수정할 때 다른 쪽도 함께 비교해야 합니다.
