package com.loopers.adapter.webapi.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.ApiResponse.Metadata.Result;
import com.loopers.adapter.webapi.brand.dto.BrandV1Dto.BrandDetailResponse;
import com.loopers.application.brand.provided.ProductPage;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;
import com.loopers.support.BaseApiTest;
import com.loopers.support.stereotype.WebApiAdapterTest;

@WebApiAdapterTest
class BrandV1ApiE2ETest extends BaseApiTest {
    @DisplayName("Get /api/v1/brands")
    @Nested
    class Get {
        private String endpointGet(Long id) {
            return "/api/v1/brands/" + id;
        }

        @DisplayName("존재하지 않는 브랜드 ID로 조회시 NOT_FOUND 반환")
        @Test
        void find_brandInfo_fail_when_given_invalid_id() {
            Member member = prepareMember();
            Brand brand = prepareBrand();
            prepareProduct(brand);

            String endpoint = endpointGet(-1L);

            ParameterizedTypeReference<ApiResponse<BrandDetailResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<BrandDetailResponse>> result = restTestClient.get()
                    .uri(endpoint)
                    .header("X-USER-ID", member.getUserId().userId())
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<BrandDetailResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();

            assertThat(response.meta().result()).isEqualTo(Result.FAIL);
        }

        @DisplayName("브랜드 ID로 브랜드 정보를 조회한다.")
        @Test
        void find_brandInfo_when_given_brandId() {
            Member member = prepareMember();
            Brand brand = brandRepository.save(
                    Brand.create("브랜드", "브랜드입니다.", LocalDate.of(1999, 1, 1)));
            Product product = prepareProduct(brand);

            String endpoint = endpointGet(brand.getId());

            ParameterizedTypeReference<ApiResponse<BrandDetailResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<BrandDetailResponse>> result = restTestClient.get()
                    .uri(endpoint)
                    .header("X-USER-ID", member.getUserId().userId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<BrandDetailResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            BrandDetailResponse expected = response.data();
            assertThat(expected).isNotNull();

            assertAll(
                    () -> assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS),
                    () -> assertThat(expected.name()).isEqualTo(brand.getName()),
                    () -> assertThat(expected.description()).isEqualTo(brand.getDescription()),
                    () -> assertThat(expected.since()).isEqualTo(brand.getBrandProfile().getSince()),
                    () -> assertThat(expected.productPage().content()).singleElement().satisfies(productInfo -> {
                        assertThat(productInfo.brandId()).isEqualTo(brand.getId());
                        assertThat(productInfo.brandName()).isEqualTo(brand.getName());
                        assertThat(productInfo.productId()).isEqualTo(product.getId());
                        assertThat(productInfo.productName()).isEqualTo(product.getName());
                        assertThat(productInfo.productDescription()).isEqualTo(product.getDescription());
                    })
            );
        }

        @DisplayName("브랜드 상품은 페이지 정보와 함께 조회한다.")
        @Test
        void find_brandInfo_with_product_page() {
            Member member = prepareMember();
            Brand brand = prepareBrand("페이지 브랜드", "페이지 브랜드입니다.");
            Product firstProduct = prepareProduct(brand, "상품1", "상품1입니다.",
                                                  BigDecimal.valueOf(1000),
                                                  ZonedDateTime.now());
            prepareProduct(brand, "상품2", "상품2입니다.",
                           BigDecimal.valueOf(2000), ZonedDateTime.now().minusMinutes(1));

            ParameterizedTypeReference<ApiResponse<BrandDetailResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<BrandDetailResponse>> result = restTestClient.get()
                    .uri(endpointGet(brand.getId()) + "?page=0&size=1")
                    .header("X-USER-ID", member.getUserId().userId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();

            ApiResponse<BrandDetailResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            ProductPage productPage = response.data().productPage();

            assertAll(
                    () -> assertThat(productPage.content()).hasSize(1),
                    () -> assertThat(productPage.content().get(0).productId()).isEqualTo(firstProduct.getId()),
                    () -> assertThat(productPage.pageNumber()).isZero(),
                    () -> assertThat(productPage.pageSize()).isEqualTo(1),
                    () -> assertThat(productPage.totalPages()).isEqualTo(2),
                    () -> assertThat(productPage.totalElements()).isEqualTo(2L)
            );
        }
    }
}
