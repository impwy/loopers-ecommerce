-- 50만건의 product와 500건의 brand 일때

CREATE INDEX idx_like_count_brand ON product (like_count, brand_id);
DROP INDEX idx_like_count_brand ON product;

CREATE INDEX idx_brand_like ON product (brand_id, like_count);
DROP INDEX idx_brand_like ON product;

CREATE INDEX idx_like_count ON product (like_count);
DROP INDEX idx_like_count ON product;

CREATE INDEX idx_brand_id ON product (brand_id);
DROP INDEX idx_brand_id ON product;


-- brand_id가 foreign key로 index설정이 되어있다.
-- brand_id를 인덱싱에 사용하고 orderby likecount는 filesort를 사용한다.
EXPLAIN ANALYZE
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id IN (1, 2, 34)
ORDER BY product.like_count DESC;

EXPLAIN
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id IN (1, 2, 34)
ORDER BY product.like_count DESC;

-- brand_id 필터 조건이 2개 이상이면 index를 사용한다.
EXPLAIN ANALYZE
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id IN (1, 2)
ORDER BY product.like_count DESC;

EXPLAIN
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id IN (1, 2)
ORDER BY product.like_count DESC;

-- 필더 갯수가 하나면 index 사용 안한다.
EXPLAIN ANALYZE
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id = 3
ORDER BY product.like_count DESC;

EXPLAIN
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id = 3
ORDER BY product.like_count DESC;

-- order by 절의 like_count는 indexing 해주지 않는다.

EXPLAIN ANALYZE
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id IN (1, 2);

EXPLAIN
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id IN (1, 2);

EXPLAIN ANALYZE
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id = 1;

EXPLAIN
SELECT *
FROM product
         LEFT JOIN brand
                   ON product.brand_id = brand.id
WHERE brand_id = 1;

ANALYZE TABLE product;
SHOW INDEX FROM product;

-- ORDER BY 적용시
-- 1. 각각 단일 index 사용시
    -- brand_id IN 조회
        -- 필터에 사용되는 brand_id만 인덱싱하고 order by like_count는 full scan한다.
    -- brand_id 단일 조회
        -- indexing을 하지 않고 full scan을 한다.
-- 2. (brand_id, like_count) 복합 index
    -- brand_id IN 조회
        -- 필터에 사용되는 brand_id만 인덱싱하고 order by like_count는 full scan한다.
    -- brand_id 단일 조회
        -- like_count Backward index scan을 한다.
-- 3. (like_count, brand_id) 복합 index
    -- brand_id IN 조회
        -- indexing을 전혀 하지 않고 full scan한다.
    -- brand_id 단일 조회
        -- indexing을 전혀 하지 않고 full scan한다.
