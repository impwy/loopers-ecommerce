package com.loopers.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.required.BrandRepository;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.ProductLikeRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.domain.product.ProductInfo;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.testcontainers.MySqlTestContainersConfig;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;

@SpringBootTest
@Import(MySqlTestContainersConfig.class)
class ProductFacadeIntegrationTest {
    @MockitoSpyBean
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private BrandRepository brandRepository;

    @MockitoSpyBean
    private ProductLikeRepository productLikeRepository;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    Product product;
    Brand brand;

    @BeforeEach
    void setUp() {
        brand = brandRepository.create(BrandFixture.createBrand());

        Product product = ProductFixture.createProduct(brand);
        product.increaseLikeCount();
        this.product = productRepository.save(product);
    }

    @DisplayName("상품 정보는 브랜드 정보, 좋아요 수를 포함한다.")
    @Test
    void productInfo_has_brandInfo_and_like_count() {
        Member member = memberRepository.save(MemberFixture.createMember());
        productLikeRepository.save(ProductLike.create(member, product));

        ProductInfo productInfo = productFacade.findProductInfo(product.getId());

        assertAll(
                () -> assertThat(productInfo.productName()).isEqualTo(product.getName()),
                () -> assertThat(productInfo.productDescription()).isEqualTo(product.getDescription()),
                () -> assertThat(productInfo.brandName()).isEqualTo(brand.getName()),
                () -> assertThat(productInfo.brandDescription()).isEqualTo(brand.getDescription()),
                () -> assertThat(productInfo.likeCount()).isOne()
        );
    }

    @DisplayName("상품 정보는 브랜드 정보, 비정규화 좋아요 수를 포함한다.")
    @Test
    void productInfo_has_brandInfo_and_like_count_denormalization() {
        ProductInfoPageResponse productInfoPageResponse
                = productFacade.findProductsInfoDenormalizationWithRedis("LIKE_COUNT_DESC",
                                                                         List.of(brand.getId()),
                                                                         PageRequest.of(0, 10));
        List<ProductInfo> content = productInfoPageResponse.content();

        assertAll(
                () -> assertThat(content.get(0).productName()).isEqualTo(product.getName()),
                () -> assertThat(content.get(0).productDescription()).isEqualTo(product.getDescription()),
                () -> assertThat(content.get(0).brandName()).isEqualTo(brand.getName()),
                () -> assertThat(content.get(0).brandDescription()).isEqualTo(brand.getDescription()),
                () -> assertThat(content.get(0).likeCount()).isOne()
        );
    }
}
