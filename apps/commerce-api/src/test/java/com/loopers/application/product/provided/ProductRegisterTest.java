package com.loopers.application.product.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.CreateProductSpec;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationValidServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationValidServiceTest
@RequiredArgsConstructor
public class ProductRegisterTest extends BaseApplicationServiceTest {
    final ProductRegister productRegister;

    Brand brand;

    @BeforeEach
    void setUp() {
        brand = prepareBrand();
    }

    @DisplayName("상품 생성 통합 테스트")
    @Test
    void createProductTest() {
        CreateProductSpec createProductSpec = ProductFixture.createProductSpec(brand);
        Product expected = productRegister.register(createProductSpec);

        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(createProductSpec.name()),
                () -> assertThat(expected.getPrice()).isEqualByComparingTo(createProductSpec.price()),
                () -> assertThat(expected.getDescription()).isEqualTo(createProductSpec.description())
        );
    }
}
