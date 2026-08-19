package com.loopers.application.brand.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.brand.required.BrandRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class BrandFinderIntegrationTest {

    @MockitoSpyBean
    private BrandRepository brandRepository;

    @Autowired
    private BrandFinder brandFinder;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("브랜드 조회 통합테스트")
    @Test
    void find_brand() {
        Brand brand = brandRepository.save(Brand.create("브랜드", "브랜드입니다."));

        Brand expected = brandFinder.find(brand.getId());

        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(brand.getName()),
                () -> assertThat(expected.getDescription()).isEqualTo(brand.getDescription()),
                () -> assertThat(expected.getId()).isEqualTo(brand.getId())
        );
    }
}
