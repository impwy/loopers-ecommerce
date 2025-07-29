package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
    Brand brand;

    @BeforeEach
    void setUp() {
        brand = brandRepository.create(BrandFixture.createBrand());
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

    @DisplayName("상품 조회 시 정렬 조건 추가: latest")
    @Test
    void sorted_product_by_latest() {
        Product firstProduct = productRepository.save(Product.create("상품1", "상품2입니다.", BigDecimal.valueOf(500),
                                                                     brand, ZonedDateTime.now().plusSeconds(1L)));
        Product secondProduct = productRepository.save(Product.create("상품2", "상품3입니다.", BigDecimal.valueOf(500),
                                                                      brand, ZonedDateTime.now().plusSeconds(2L)));
        Product thirdProduct = productRepository.save(Product.create("상품3", "상품4입니다.", BigDecimal.valueOf(500),
                                                                     brand, ZonedDateTime.now().plusSeconds(3L)));

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
        Product firstProduct = productRepository.save(Product.create("상품1", "상품2입니다.", BigDecimal.valueOf(600),
                                                                     brand, ZonedDateTime.now()));
        Product secondProduct = productRepository.save(Product.create("상품2", "상품3입니다.", BigDecimal.valueOf(700),
                                                                      brand, ZonedDateTime.now()));
        Product thirdProduct = productRepository.save(Product.create("상품3", "상품4입니다.", BigDecimal.valueOf(800),
                                                                     brand, ZonedDateTime.now()));

        Sort priceSort = Sort.by(Direction.ASC, "price");
        List<Product> products = productFinder.findByConditions(priceSort);

        assertThat(products)
                .extracting(Product::getName)
                .containsExactly(product.getName(),
                                 firstProduct.getName(),
                                 secondProduct.getName(),
                                 thirdProduct.getName());
    }
}
