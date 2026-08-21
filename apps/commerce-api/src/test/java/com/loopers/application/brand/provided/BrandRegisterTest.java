package com.loopers.application.brand.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationValidServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationValidServiceTest
@RequiredArgsConstructor
class BrandRegisterTest extends BaseApplicationServiceTest {
    final BrandRegister brandRegister;

    @DisplayName("브랜드 생성 통합테스트")
    @Test
    void createBrand() {
        var request = BrandFixture.createBrandCreateRequest();
        Brand brand = brandRegister.create(request);

        assertAll(
                () -> assertThat(brand.getName()).isEqualTo(request.name()),
                () -> assertThat(brand.getDescription()).isEqualTo(request.description()),
                () -> assertThat(brand.getBrandProfile().getSince()).isNotNull()
        );
    }
}
