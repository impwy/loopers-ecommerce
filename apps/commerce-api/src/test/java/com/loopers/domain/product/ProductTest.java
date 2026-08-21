package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loopers.domain.brand.BrandFixture;

class ProductTest {

    @DisplayName("상품을 만든다")
    @Test
    void createProduct() {
        CreateProductSpec spec = ProductFixture.createProductSpec(BrandFixture.createBrand());
        Product product = spec.toEntity();

        assertThat(product.getName()).isEqualTo(spec.name());
        assertThat(product.getDescription()).isEqualTo(spec.description());
        assertThat(product.getPrice()).isEqualByComparingTo(spec.price());
    }

    @DisplayName("좋아요 수는 0 미만으로 감소할 수 없다")
    @Test
    void decrease_like_count_fail_when_like_count_is_zero() {
        Product product = ProductFixture.createProduct(BrandFixture.createBrand());

        assertThatThrownBy(product::decreaseLikeCount)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("좋아요 수를 증가한 뒤 감소할 수 있다")
    @Test
    void decrease_like_count_after_increase() {
        Product product = ProductFixture.createProduct(BrandFixture.createBrand());
        product.increaseLikeCount();

        product.decreaseLikeCount();

        assertThat(product.getLikeCount()).isZero();
    }
}
