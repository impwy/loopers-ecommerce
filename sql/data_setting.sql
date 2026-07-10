-- 로컬 성능 테스트 전용 데이터입니다. commerce-api의 기존 비즈니스 데이터를 모두 삭제합니다.
-- FK 부모/자식 테이블을 함께 초기화하므로 제약 조건 검사를 잠시 비활성화합니다.
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE loopers.product_event_outbox;
TRUNCATE TABLE loopers.payments;
TRUNCATE TABLE loopers.order_item;
TRUNCATE TABLE loopers.orders;
TRUNCATE TABLE loopers.member_coupon;
TRUNCATE TABLE loopers.coupon;
TRUNCATE TABLE loopers.inventory;
TRUNCATE TABLE loopers.product_like;
TRUNCATE TABLE loopers.product;
TRUNCATE TABLE loopers.member;
TRUNCATE TABLE loopers.point;
TRUNCATE TABLE loopers.brand;
SET FOREIGN_KEY_CHECKS = 1;

-- brand 테이블 데이터 삽입
SET SESSION cte_max_recursion_depth = 500;
INSERT INTO loopers.brand (created_at, updated_at, name, description)
WITH RECURSIVE seq AS (SELECT 0 AS n
                       UNION ALL
                       SELECT n + 1
                       FROM seq
                       WHERE n < 499)
SELECT NOW(),
    NOW(),
    CONCAT('Brand ', n),
    CONCAT('Description for brand ', n)
FROM seq;

-- product 테이블 데이터 삽입
SET SESSION cte_max_recursion_depth = 300000;
INSERT INTO loopers.product (price, brand_id, created_at, updated_at, name, description, latest_at, like_count)
WITH RECURSIVE seq AS (SELECT 0 AS n
                       UNION ALL
                       SELECT n + 1
                       FROM seq
                       WHERE n < 299999)
SELECT MOD(n * 37, 300000) / 100,
    1 + MOD(n, 500),
    NOW(),
    NOW(),
    CONCAT('Product ', n),
    CONCAT('Description for product ', n),
    NOW(),
    0
FROM seq;

-- member 테이블 데이터 삽입
SET SESSION cte_max_recursion_depth = 10000;
INSERT INTO loopers.member (created_at, updated_at, gender, email, member_id, password_hash)
WITH RECURSIVE seq AS (SELECT 0 AS n
                       UNION ALL
                       SELECT n + 1
                       FROM seq
                       WHERE n < 9999)
SELECT NOW(),
    NOW(),
    'MALE',
    CONCAT('user', n, '@example.com'),
    CONCAT('member', n),
    'hashed_password'
FROM seq;
-- product_like 테이블 데이터 삽입
SET SESSION cte_max_recursion_depth = 300000;
INSERT INTO loopers.product_like (created_at, updated_at, member_id, product_id)
WITH RECURSIVE seq AS (SELECT 1 AS n
                       UNION ALL
                       SELECT n + 1
                       FROM seq
                       WHERE n < 300000)
SELECT NOW(),
    NOW(),
    1 + MOD(n - 1, 10000),
    1 + FLOOR(300000 * POW((n - 1) / 300000.0, 2))
FROM seq;

-- 정규화/비정규화 조회가 같은 좋아요 수를 반환하도록 집계 컬럼을 동기화합니다.
UPDATE loopers.product product
    LEFT JOIN (
        SELECT product_id, COUNT(*) AS like_count
        FROM loopers.product_like
        GROUP BY product_id
    ) product_like_count ON product_like_count.product_id = product.id
SET product.like_count = COALESCE(product_like_count.like_count, 0);
