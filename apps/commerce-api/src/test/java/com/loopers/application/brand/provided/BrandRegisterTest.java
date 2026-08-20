package com.loopers.application.brand.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.brand.required.BrandRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.support.stereotype.ApplicationValidServiceTest;
import com.loopers.utils.DatabaseCleanUp;

import lombok.RequiredArgsConstructor;

@ApplicationValidServiceTest
@RequiredArgsConstructor
class BrandRegisterTest {
    @MockitoSpyBean
    private BrandRepository brandRepository;

    final BrandRegister brandRegister;

    final DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("브랜드 생성 통합테스트")
    @Test
    void createBrand() {
        Brand brand = brandRegister.create(BrandFixture.createBrandCreateRequest());

        assertAll(
                () -> assertThat(brand.getName()).isEqualTo("브랜드"),
                () -> assertThat(brand.getDescription()).isEqualTo("브랜드입니다."),
                () -> assertThat(brand.getBrandProfile().getSince()).isNotNull()
        );
    }
}
