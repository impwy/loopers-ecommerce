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
class ProductFinderIntegrationTest {

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private ProductFinder productFinder;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품을 등록한다.")
    @Test
    void find_product() {
        Product product = productRepository.save(ProductFixture.createProduct());

        Product expected = productFinder.find(product.getId());

        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(product.getName()),
                () -> assertThat(expected.getDescription()).isEqualTo(product.getDescription()),
                () -> assertThat(expected.getPrice().longValue()).isEqualTo(product.getPrice().longValue())
        );
    }
}
