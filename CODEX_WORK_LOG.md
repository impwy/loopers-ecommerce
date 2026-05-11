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
- `ProductFacadeTest`를 추가해 좋아요 증가/감소 시 outbox가 각각 증가/감소 이벤트 타입으로 생성되고 `LikeIncrease`/`LikeDecrease` 이벤트가 발행되는지 검증했습니다.

### 필요한 테스트

- `ProductTest`: 좋아요 수가 음수가 되지 않는 도메인 불변식
- `ProductFacadeTest`: 좋아요 증가/감소 outbox/event 타입 정합성

### 추가로 얻은 지식과 확인할 점

- 좋아요 증감은 단순 카운터 변경이 아니라 outbox, async event handler, Kafka producer, streamer 메트릭까지 연결됩니다.
- 이후 추가 확인 대상으로는 좋아요 취소 API가 상품 like count 감소까지 호출하는지, Redis 캐시와 DB 카운트가 함께 갱신되는지 확인할 필요가 있습니다.
