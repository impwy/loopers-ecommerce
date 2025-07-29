package com.loopers.domain.product;

import java.math.BigDecimal;

import com.loopers.domain.brand.Brand;

public class ProductFixture {
    public static Product createProduct(Brand brand) {
        return Product.create("상품", "상품입니다.", BigDecimal.valueOf(500), brand);
    }
}
