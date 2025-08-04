package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loopers.domain.brand.BrandFixture;

class ProductTest {

    @DisplayName("상품을 만든다")
    @Test
    void createProduct() {
        Product product = ProductFixture.createProduct(BrandFixture.createBrand());

        assertThat(product.getName()).isEqualTo("상품");
        assertThat(product.getDescription()).isEqualTo("상품입니다.");
        assertThat(product.getPrice().compareTo(BigDecimal.valueOf(500))).isZero();
    }
}
