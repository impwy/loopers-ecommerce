-- 성능 실험 전용 데이터입니다. local 프로필로 스키마를 새로 만든 직후에만 실행하세요.
-- 기존 데이터를 보존해야 하는 환경에서는 실행하지 마세요.
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE loopers.product_like;
TRUNCATE TABLE loopers.product;
TRUNCATE TABLE loopers.brand;
TRUNCATE TABLE loopers.member;
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
SELECT ROUND(RAND() * 3000, 2),
    1 + FLOOR(RAND() * 500),
    NOW(),
    NOW(),
    CONCAT('Product ', n),
    CONCAT('Description for product ', n),
    NOW(),
    FLOOR(RAND() * 300000)
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
                       WHERE n < 299999)
SELECT NOW(),
    NOW(),
    1 + FLOOR(RAND() * 10000),
    1 + FLOOR(RAND() * 300000)
FROM seq;

-- 정규화 집계값과 비정규화 like_count가 같은 의미를 갖도록 맞춥니다.
UPDATE loopers.product p
LEFT JOIN (
    SELECT product_id, COUNT(*) AS like_count
    FROM loopers.product_like
    GROUP BY product_id
) pl ON pl.product_id = p.id
SET p.like_count = COALESCE(pl.like_count, 0);

ANALYZE TABLE loopers.brand, loopers.product, loopers.member, loopers.product_like;
