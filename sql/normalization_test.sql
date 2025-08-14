-- 50만건의 product와 500건의 brand 일때

CREATE INDEX idx_like_count_brand ON product (like_count, brand_id);
DROP INDEX idx_like_count_brand ON product;

CREATE INDEX idx_brand_like ON product (brand_id, like_count);
DROP INDEX idx_brand_like ON product;

CREATE INDEX idx_like_count ON product (like_count);
DROP INDEX idx_like_count ON product;

CREATE INDEX idx_brand_id ON product (brand_id);
DROP INDEX idx_brand_id ON product;

CREATE INDEX idx_product_id ON product_like (product_id);
DROP INDEX idx_product_id ON product_like;

-- ----------------------------------------
EXPLAIN ANALYZE
SELECT p.*,
       COUNT(pl.product_id) AS like_c
FROM product p
         LEFT JOIN brand b
                   ON p.brand_id = b.id
         LEFT JOIN product_like pl
                   ON pl.product_id = p.id
WHERE b.id IN (127, 315, 248)
GROUP BY p.id
ORDER BY like_c DESC;

EXPLAIN
SELECT p.*,
       COUNT(pl.product_id) AS like_c
FROM product p
         LEFT JOIN brand b
                   ON p.brand_id = b.id
         LEFT JOIN product_like pl
                   ON pl.product_id = p.id
WHERE b.id IN (127, 315, 248)
GROUP BY p.id
ORDER BY like_c DESC;

-- ----------------------------------------
EXPLAIN ANALYZE
SELECT p.*,
       COUNT(pl.product_id) AS like_c
FROM product p
         LEFT JOIN brand b
                   ON p.brand_id = b.id
         LEFT JOIN product_like pl
                   ON pl.product_id = p.id
WHERE b.id = 127
GROUP BY p.id
ORDER BY like_c DESC;

EXPLAIN
SELECT p.*,
       COUNT(pl.product_id) AS like_c
FROM product p
         LEFT JOIN brand b
                   ON p.brand_id = b.id
         LEFT JOIN product_like pl
                   ON pl.product_id = p.id
WHERE b.id = 127
GROUP BY p.id
ORDER BY like_c DESC;

-- ----------------------------------------
EXPLAIN ANALYZE
SELECT p.*,
       COUNT(pl.product_id) AS like_c
FROM product p
         LEFT JOIN brand b
                   ON p.brand_id = b.id
         LEFT JOIN product_like pl
                   ON pl.product_id = p.id
WHERE b.id IN (127, 315, 248)
GROUP BY p.id;

EXPLAIN
SELECT p.*,
       COUNT(pl.product_id) AS like_c
FROM product p
         LEFT JOIN brand b
                   ON p.brand_id = b.id
         LEFT JOIN product_like pl
                   ON pl.product_id = p.id
WHERE b.id IN (127, 315, 248)
GROUP BY p.id;

-- ----------------------------------------
EXPLAIN ANALYZE
SELECT p.*,
       COUNT(pl.product_id) AS like_c
FROM product p
         LEFT JOIN brand b
                   ON p.brand_id = b.id
         LEFT JOIN product_like pl
                   ON pl.product_id = p.id
WHERE b.id = 127
GROUP BY p.id;

EXPLAIN
SELECT p.*,
       COUNT(pl.product_id) AS like_c
FROM product p
         LEFT JOIN brand b
                   ON p.brand_id = b.id
         LEFT JOIN product_like pl
                   ON pl.product_id = p.id
WHERE b.id = 127
GROUP BY p.id;

ANALYZE TABLE product;
SHOW INDEX FROM product;
