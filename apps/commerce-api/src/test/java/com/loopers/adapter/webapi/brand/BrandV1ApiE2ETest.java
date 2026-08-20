package com.loopers.adapter.webapi.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.ApiResponse.Metadata.Result;
import com.loopers.adapter.webapi.brand.dto.BrandV1Dto.Response.BrandInfoResponse;
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

            ParameterizedTypeReference<ApiResponse<BrandInfoResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<BrandInfoResponse>> result = restTestClient.get()
                    .uri(endpoint)
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<BrandInfoResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();

            assertThat(response.meta().result()).isEqualTo(Result.FAIL);
        }

        @DisplayName("브랜드 ID로 브랜드 정보를 조회한다.")
        @Test
        void find_brandInfo_when_given_brandId() {
            Member member = prepareMember();
            Brand brand = prepareBrand();
            Product product = prepareProduct(brand);

            String endpoint = endpointGet(brand.getId());

            ParameterizedTypeReference<ApiResponse<BrandInfoResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<BrandInfoResponse>> result = restTestClient.get()
                    .uri(endpoint)
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<BrandInfoResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            BrandInfoResponse expected = response.data();
            assertThat(expected).isNotNull();

            assertAll(
                    () -> assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS),
                    () -> assertThat(expected.productInfos()).singleElement().satisfies(productInfo -> {
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
