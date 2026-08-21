package com.loopers.application.brand.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loopers.application.brand.BrandCreateRequest;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationServiceTest
@RequiredArgsConstructor
class BrandFinderTest extends BaseApplicationServiceTest {
    final BrandFinder brandFinder;

    @DisplayName("브랜드 조회 통합테스트")
    @Test
    void find_brand() {
        BrandCreateRequest request = BrandFixture.createBrandCreateRequest("브랜드", "브랜드입니다.",
                                                                           java.time.LocalDate.of(1999, 1, 1));
        Brand brand = prepareBrand(request);

        Brand expected = brandFinder.find(brand.getId());

        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(brand.getName()),
                () -> assertThat(expected.getDescription()).isEqualTo(brand.getDescription()),
                () -> assertThat(expected.getId()).isEqualTo(brand.getId())
        );
    }
}
