package com.loopers.domain.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BrandTest {

    @DisplayName("브랜드 생성 테스트")
    @Test
    void createBrand() {
        Brand brand = Brand.create("브랜드", "브랜드입니다.");

        assertThat(brand.getName()).isEqualTo("브랜드");
        assertThat(brand.getDescription()).isEqualTo("브랜드입니다.");
    }
}
