package com.loopers.interfaces.api.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.loopers.application.required.BrandRepository;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.ApiResponse.Metadata.Result;
import com.loopers.interfaces.api.brand.dto.BrandV1Dto.Response.BrandInfoResponse;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrandV1ApiE2ETest {
    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Autowired
    BrandV1ApiE2ETest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp,
                      BrandRepository brandRepository, ProductRepository productRepository,
                      MemberRepository memberRepository) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("Get /api/v1/brands")
    @Nested
    class Get {
        private final Function<Long, String> ENDPOINT_GET = id -> "/api/v1/brands/" + id;

        @DisplayName("존재하지 않는 브랜드 ID로 조회시 NOT_FOUND 반환")
        @Test
        void find_brandInfo_fail_when_given_invalid_id() {
            Member member = memberRepository.save(MemberFixture.createMember());
            Brand brand = brandRepository.create(BrandFixture.createBrand());
            productRepository.save(ProductFixture.createProduct(brand));

            String enPoint = ENDPOINT_GET.apply(-1L);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ParameterizedTypeReference<ApiResponse<BrandInfoResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            ResponseEntity<ApiResponse<BrandInfoResponse>> response =
                    testRestTemplate.exchange(RequestEntity.get(enPoint)
                                                           .header("X-USER-ID", member.getMemberId().memberId())
                                                           .build(),
                                              responseType);

            assertThat(response.getBody().meta().result()).isEqualTo(Result.FAIL);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @DisplayName("브랜드 ID로 브랜드 정보를 조회한다.")
        @Test
        void find_brandInfo_when_given_brandId() {
            Member member = memberRepository.save(MemberFixture.createMember());
            Brand brand = brandRepository.create(BrandFixture.createBrand());
            Product product = productRepository.save(ProductFixture.createProduct(brand));

            String enPoint = ENDPOINT_GET.apply(brand.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ParameterizedTypeReference<ApiResponse<BrandInfoResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            ResponseEntity<ApiResponse<BrandInfoResponse>> response =
                    testRestTemplate.exchange(RequestEntity.get(enPoint)
                                                           .header("X-USER-ID", member.getMemberId().memberId())
                                                           .build(),
                                              responseType);

            BrandInfoResponse expected = response.getBody().data();

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS),
                    () -> expected.productInfos().forEach(productInfo -> {
                        assertThat(productInfo.brandId()).isEqualTo(brand.getId());
                        assertThat(productInfo.brandName()).isEqualTo(brand.getName());
                        assertThat(productInfo.brandDescription()).isEqualTo(brand.getDescription());
                        assertThat(productInfo.productId()).isEqualTo(product.getId());
                        assertThat(productInfo.productName()).isEqualTo(product.getName());
                        assertThat(productInfo.productDescription()).isEqualTo(product.getDescription());
                    })
            );
        }
    }
}
