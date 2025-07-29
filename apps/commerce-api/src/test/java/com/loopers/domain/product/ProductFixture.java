package com.loopers.domain.product;

import java.math.BigDecimal;

public class ProductFixture {
    public static Product createProduct() {
        return Product.create("상품", "상품입니다.", BigDecimal.valueOf(500));
    }
}
