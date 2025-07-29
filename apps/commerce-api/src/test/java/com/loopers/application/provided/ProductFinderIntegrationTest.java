package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.ProductFacade;
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
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class ProductFinderIntegrationTest {

    @MockitoSpyBean
    private ProductRepository productRepository;

    @MockitoSpyBean
    private BrandRepository brandRepository;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private ProductLikeRepository productLikeRepository;

    @Autowired
    private ProductFinder productFinder;

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    Product product;

    @BeforeEach
    void setUp() {
        Brand brand = brandRepository.create(BrandFixture.createBrand());
        product = productRepository.save(ProductFixture.createProduct(brand));

    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
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

    @DisplayName("상품 정보는 브랜드 정보, 좋아요 수를 포함한다.")
    @Test
    void productInfo_has_brandInfo_and_like_count() {
        Member member = memberRepository.save(MemberFixture.createMember());
        Brand brand = brandRepository.create(Brand.create("브랜드", "브랜드입니다."));
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
}
