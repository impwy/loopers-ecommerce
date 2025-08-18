package com.loopers.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.provided.ProductRegister;
import com.loopers.application.required.BrandRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.product.CreateProductSpec;
import com.loopers.domain.product.Product;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
public class ProductRegisterIntegrationTest {
    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private ProductRegister productRegister;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @MockitoSpyBean
    private BrandRepository brandRepository;

    Brand brand;

    @BeforeEach
    void setUp() {
        brand = brandRepository.create(BrandFixture.createBrand());
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 생성 통합 테스트")
    @Test
    void createProductTest() {
        CreateProductSpec createProductSpec = new CreateProductSpec("상품", "상품입니다.", BigDecimal.valueOf(500), brand, ZonedDateTime.now());
        Product expected = productRegister.register(createProductSpec);

        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(createProductSpec.name()),
                () -> assertThat(expected.getPrice().abs()).isEqualTo(createProductSpec.price().abs()),
                () -> assertThat(expected.getDescription()).isEqualTo(createProductSpec.description())
        );
    }
}
