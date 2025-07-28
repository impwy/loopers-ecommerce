package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.required.BrandRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
public class BrandRegisterIntegrationTest {

    @MockitoSpyBean
    private BrandRepository brandRepository;

    @Autowired
    private BrandRegister brandRegister;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("브랜드 생성 통합테스트")
    @Test
    void createBrand() {
        Brand brand = brandRegister.create(Brand.create("브랜드", "브랜드입니다."));

        assertAll(
                () -> assertThat(brand.getName()).isEqualTo("브랜드"),
                () -> assertThat(brand.getDescription()).isEqualTo("브랜드입니다.")
        );
    }
}
