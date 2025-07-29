package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.required.ProductRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
public class ProductRegisterIntegrationTest {
    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private ProductRegister productRegister;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 생성 통합 테스트")
    @Test
    void createProductTest() {
        Product product = productRepository.save(ProductFixture.createProduct());

        Product expected = productRegister.register(product);

        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(product.getName()),
                () -> assertThat(expected.getPrice().abs()).isEqualTo(product.getPrice().abs()),
                () -> assertThat(expected.getDescription()).isEqualTo(product.getDescription())
        );
    }
}
