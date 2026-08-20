package com.loopers.domain.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BrandTest {

    @DisplayName("브랜드 생성 테스트")
    @Test
    void createBrand() {
        LocalDate since = LocalDate.of(1999, 1, 1);
        Brand brand = Brand.create("브랜드", "브랜드입니다.", since);

        assertThat(brand.getName()).isEqualTo("브랜드");
        assertThat(brand.getDescription()).isEqualTo("브랜드입니다.");
        assertThat(brand.getName()).isEqualTo("브랜드");
        assertThat(brand.getDescription()).isEqualTo("브랜드입니다.");
        assertThat(brand.getBrandProfile().getSince()).isEqualTo(since);
    }

    @DisplayName("브랜드 프로필의 이름과 설립일은 필수다.")
    @Test
    void createBrand_requires_profile_values() {
        assertThatThrownBy(() -> Brand.create(null, "브랜드입니다.", LocalDate.of(1999, 1, 1)))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Brand.create("브랜드", "브랜드입니다.", null))
                .isInstanceOf(NullPointerException.class);
    }
}
