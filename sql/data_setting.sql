-- brand 테이블 초기화 & 데이터 삽입
TRUNCATE TABLE loopers.brand;

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

-- product 테이블 초기화 & 데이터 삽입
TRUNCATE TABLE loopers.product;

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

-- member 테이블 초기화 & 데이터 삽입
TRUNCATE TABLE loopers.member;

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


-- product_like 테이블 초기화 & 데이터 삽입
TRUNCATE TABLE loopers.product_like;

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