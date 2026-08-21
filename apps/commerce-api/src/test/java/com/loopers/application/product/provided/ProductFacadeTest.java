package com.loopers.application.product.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Page;

import com.loopers.application.product.ProductFacade;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductInfoWithRank;
import com.loopers.utils.RedisCleanUp;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationServiceTest;
import com.loopers.testcontainers.RedisTestContainersConfig;

@ApplicationServiceTest
@Import(RedisTestContainersConfig.class)
class ProductFacadeTest extends BaseApplicationServiceTest {
    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductRegister productRegister;

    @Autowired
    private RedisCleanUp redisCleanUp;

    Product product;
    Brand brand;

    @AfterEach
    void cleanUpRedis() {
        redisCleanUp.truncateAll();
    }

    @BeforeEach
    void setUp() {
        brand = prepareBrand();
        product = prepareProduct(brand);
        product = productRegister.increaseLike(product.getId());
    }

    @DisplayName("상품 정보는 브랜드 정보, 좋아요 수를 포함한다.")
    @Test
    void productInfo_has_brandInfo_and_like_count() {
        Member member = prepareMember();
        prepareProductLike(member, product);

        ProductInfoWithRank productInfoWithRank = productFacade.findProductInfo(product.getId());
        ProductInfo productInfo = productInfoWithRank.productInfo();

        assertAll(
                () -> assertThat(productInfo.productName()).isEqualTo(product.getName()),
                () -> assertThat(productInfo.productDescription()).isEqualTo(product.getDescription()),
                () -> assertThat(productInfo.brandName()).isEqualTo(brand.getName()),
                () -> assertThat(productInfo.likeCount()).isOne()
        );
    }

    @DisplayName("상품 정보는 브랜드 정보, 비정규화 좋아요 수를 포함한다.")
    @Test
    void productInfo_has_brandInfo_and_like_count_denormalization() {
        Page<ProductInfo> productInfoPageResponse
                = productFacade.findProductsInfoDenormalizationWithRedis("LIKE_COUNT_DESC",
                                                                         List.of(brand.getId()),
                                                                         PageRequest.of(0, 10));
        List<ProductInfo> content = productInfoPageResponse.getContent();

        assertAll(
                () -> assertThat(content.get(0).productName()).isEqualTo(product.getName()),
                () -> assertThat(content.get(0).productDescription()).isEqualTo(product.getDescription()),
                () -> assertThat(content.get(0).brandName()).isEqualTo(brand.getName()),
                () -> assertThat(content.get(0).likeCount()).isOne()
        );
    }
}
