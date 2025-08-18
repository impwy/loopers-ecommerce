package com.loopers.interfaces.api.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.loopers.application.required.BrandRepository;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductV1ApiE2ETest {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final DatabaseCleanUp databaseCleanUp;
    private final TestRestTemplate testRestTemplate;

    @Autowired
    ProductV1ApiE2ETest(MemberRepository memberRepository, ProductRepository productRepository,
                        BrandRepository brandRepository, DatabaseCleanUp databaseCleanUp,
                        TestRestTemplate testRestTemplate) {
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.databaseCleanUp = databaseCleanUp;
        this.testRestTemplate = testRestTemplate;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    class Get {
        private final static String ENDPOINT_GET = "/api/v1/products";

        @DisplayName("상품 목록 조회 시 성공")
        @Test
        void find_products_test() {
            Member member = memberRepository.save(MemberFixture.createMember());
            Brand brand = brandRepository.create(BrandFixture.createBrand());
            Product firstProduct = productRepository.save(Product.create("상품1", "상품1입니다.", BigDecimal.valueOf(500), brand, ZonedDateTime.now()));
            Product secondProduct = productRepository.save(Product.create("상품2", "상품2입니다.", BigDecimal.valueOf(600), brand, ZonedDateTime.now()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", member.getMemberId().memberId());

            ParameterizedTypeReference<ApiResponse<ProductInfoPageResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<ProductInfoPageResponse>> response =
                    testRestTemplate.exchange(ENDPOINT_GET + "?page=0&brandIds="+ brand.getId() +" &size=2&sort=latestAt", HttpMethod.GET, new HttpEntity<>(headers), responseType);

            List<ProductInfo> productInfos = response.getBody().data().content();

            assertAll(
                    () -> assertThat(productInfos.getFirst().productId()).isEqualTo(secondProduct.getId()),
                    () -> assertThat(productInfos.getFirst().productName()).isEqualTo(secondProduct.getName()),
                    () -> assertThat(productInfos.getFirst().productDescription()).isEqualTo(secondProduct.getDescription()),
                    () -> assertThat(productInfos.get(1).productId()).isEqualTo(firstProduct.getId()),
                    () -> assertThat(productInfos.get(1).productName()).isEqualTo(firstProduct.getName()),
                    () -> assertThat(productInfos.get(1).productDescription()).isEqualTo(firstProduct.getDescription()),
                    () -> assertThat(response.getBody().data().pageSize()).isEqualTo(2),
                    () -> assertThat(response.getBody().data().totalElements()).isEqualTo(productInfos.size())
            );
        }
    }
}
