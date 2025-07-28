package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

    @DisplayName("상품을 만든다")
    @Test
    void createProduct() {
        Product product = Product.create("상품1", "상품입니다.", BigDecimal.valueOf(500));

        assertThat(product.getName()).isEqualTo("상품1");
        assertThat(product.getDescription()).isEqualTo("상품입니다.");
        assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(500));
    }
}
