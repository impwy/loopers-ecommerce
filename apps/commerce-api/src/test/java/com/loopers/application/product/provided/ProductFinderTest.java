package com.loopers.application.product.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationServiceTest;

@ApplicationServiceTest
class ProductFinderTest extends BaseApplicationServiceTest {
    @Autowired
    private ProductFinder productFinder;

    private static final ZonedDateTime BASE_TIME =
            ZonedDateTime.of(2025, 7, 13, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));

    Product product;
    Brand brand;

    @BeforeEach
    void setUp() {
        brand = prepareBrand();
        product = prepareProduct(brand, "기본 상품", "기본 상품입니다.",
                                  BigDecimal.valueOf(500), BASE_TIME);

    }

    @DisplayName("상품을 조회한다.")
    @Test
    void find_product() {
        Product expected = productFinder.find(product.getId());

        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(product.getName()),
                () -> assertThat(expected.getDescription()).isEqualTo(product.getDescription()),
                () -> assertThat(expected.getPrice().longValue()).isEqualTo(product.getPrice().longValue())
        );
    }

    @DisplayName("상품 정보 조회 성공 테스트")
    @Test
    void productInfo_has_brandInfo_and_like_count() {
        Product expected = productFinder.find(product.getId());

        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(product.getName()),
                () -> assertThat(expected.getPrice().compareTo(product.getPrice())).isZero(),
                () -> assertThat(expected.getDescription()).isEqualTo(product.getDescription())
        );
    }

    @DisplayName("상품 조회 시 정렬 조건 추가: latest")
    @Test
    void sorted_product_by_latest() {
        Product firstProduct = prepareProduct(brand, "상품1", "상품2입니다.", BigDecimal.valueOf(500),
                                              BASE_TIME.plusSeconds(1L));
        Product secondProduct = prepareProduct(brand, "상품2", "상품3입니다.", BigDecimal.valueOf(500),
                                               BASE_TIME.plusSeconds(2L));
        Product thirdProduct = prepareProduct(brand, "상품3", "상품4입니다.", BigDecimal.valueOf(500),
                                              BASE_TIME.plusSeconds(3L));

        Sort latestAtSort = Sort.by(Direction.DESC, "latestAt");
        List<Product> products = productFinder.findByConditions(latestAtSort);

        assertThat(products)
                .extracting(Product::getName)
                .containsExactly(thirdProduct.getName(),
                                 secondProduct.getName(),
                                 firstProduct.getName(),
                                 product.getName());
    }

    @DisplayName("상품 조회 시 정렬 조건 추가: price")
    @Test
    void sorted_product_by_price() {
        Product firstProduct = prepareProduct(brand, "상품1", "상품2입니다.", BigDecimal.valueOf(600), BASE_TIME);
        Product secondProduct = prepareProduct(brand, "상품2", "상품3입니다.", BigDecimal.valueOf(700), BASE_TIME);
        Product thirdProduct = prepareProduct(brand, "상품3", "상품4입니다.", BigDecimal.valueOf(800), BASE_TIME);

        Sort priceSort = Sort.by(Direction.ASC, "price");
        List<Product> products = productFinder.findByConditions(priceSort);

        assertThat(products)
                .extracting(Product::getName)
                .containsExactly(product.getName(),
                                 firstProduct.getName(),
                                 secondProduct.getName(),
                                 thirdProduct.getName());
    }

    @DisplayName("브랜드 필터 상품 조회 시 전체 개수는 필터 조건을 반영한다")
    @Test
    void find_with_like_count_total_count_applies_brand_filter() {
        Brand otherBrand = prepareBrand("다른 브랜드");
        prepareProduct(otherBrand, "다른 브랜드 상품", "다른 브랜드 상품입니다.", BigDecimal.valueOf(700), BASE_TIME);

        Page<ProductInfo> products =
                productFinder.findWithLikeCount("latestAt", List.of(brand.getId()), PageRequest.of(0, 10));

        assertAll(
                () -> assertThat(products.getContent())
                        .extracting(ProductInfo::productId)
                        .containsExactly(product.getId()),
                () -> assertThat(products.getContent().get(0).brandId()).isEqualTo(brand.getId()),
                () -> assertThat(products.getContent().get(0).brandName()).isEqualTo(brand.getName()),
                () -> assertThat(products.getTotalElements()).isEqualTo(1)
        );
    }
}
