package com.loopers.adapter.webapi.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductInfoWithRank;
import com.loopers.support.BaseApiTest;
import com.loopers.support.stereotype.WebApiAdapterTest;
import com.loopers.utils.RedisCleanUp;

@WebApiAdapterTest
class ProductV1ApiE2ETest extends BaseApiTest {
    private static final String ENDPOINT_GET = "/api/v1/products";

    @Autowired
    private RedisCleanUp redisCleanUp;

    protected void cleanRedis() {
        redisCleanUp.truncateAll();
    }

    @Nested
    class Get {
        @DisplayName("상품 상세 조회 시 성공")
        @Test
        void find_product_test() {
            Member member = prepareMember();
            Brand brand = prepareBrand();
            Product product = prepareProduct(brand);

            ParameterizedTypeReference<ApiResponse<ProductInfoWithRank>> responseType =
                    new ParameterizedTypeReference<>() {};
            EntityExchangeResult<ApiResponse<ProductInfoWithRank>> result = restTestClient.get()
                    .uri(ENDPOINT_GET + "/" + product.getId())
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<ProductInfoWithRank> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();
            ProductInfo productInfo = response.data().productInfo();

            assertAll(
                    () -> assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS),
                    () -> assertThat(productInfo.productId()).isEqualTo(product.getId()),
                    () -> assertThat(productInfo.brandId()).isEqualTo(brand.getId()),
                    () -> assertThat(productInfo.productName()).isEqualTo(product.getName()),
                    () -> assertThat(productInfo.productDescription()).isEqualTo(product.getDescription()),
                    () -> assertThat(productInfo.brandName()).isEqualTo(brand.getName()),
                    () -> assertThat(productInfo.brandDescription()).isEqualTo(brand.getDescription())
            );
        }

        @DisplayName("존재하지 않는 상품 ID로 조회 시 NOT_FOUND 반환")
        @Test
        void find_product_fail_when_given_invalid_id() {
            Member member = prepareMember();
            ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<Object>> result = restTestClient.get()
                    .uri(ENDPOINT_GET + "/-1")
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<Object> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.FAIL);
        }

        @DisplayName("상품 목록 조회 시 성공")
        @Test
        void find_products_test() {
            Member member = prepareMember();
            Brand brand = prepareBrand();
            Brand anotherBrand = prepareBrand("다른 브랜드", "다른 브랜드입니다.");
            Product firstProduct = prepareProduct(brand, "상품1", "상품1입니다.",
                    BigDecimal.valueOf(500), ZonedDateTime.parse("2025-01-01T00:00:00Z"));
            Product secondProduct = prepareProduct(brand, "상품2", "상품2입니다.",
                    BigDecimal.valueOf(600), ZonedDateTime.parse("2025-01-02T00:00:00Z"));
            prepareProduct(anotherBrand, "다른 브랜드 상품", "다른 브랜드 상품입니다.",
                    BigDecimal.valueOf(700), ZonedDateTime.parse("2025-01-03T00:00:00Z"));

            ParameterizedTypeReference<ApiResponse<ProductInfoPageResponse>> responseType =
                    new ParameterizedTypeReference<>() {};
            EntityExchangeResult<ApiResponse<ProductInfoPageResponse>> result = restTestClient.get()
                    .uri(ENDPOINT_GET + "?page=0&brandIds=" + brand.getId() + "&size=2&sort=latestAt")
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<ProductInfoPageResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();
            List<ProductInfo> productInfos = response.data().content();

            assertAll(
                    () -> assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS),
                    () -> assertThat(productInfos.getFirst().productId()).isEqualTo(secondProduct.getId()),
                    () -> assertThat(productInfos.getFirst().productName()).isEqualTo(secondProduct.getName()),
                    () -> assertThat(productInfos.getFirst().productDescription()).isEqualTo(secondProduct.getDescription()),
                    () -> assertThat(productInfos.get(1).productId()).isEqualTo(firstProduct.getId()),
                    () -> assertThat(productInfos.get(1).productName()).isEqualTo(firstProduct.getName()),
                    () -> assertThat(productInfos.get(1).productDescription()).isEqualTo(firstProduct.getDescription()),
                    () -> assertThat(response.data().pageSize()).isEqualTo(2),
                    () -> assertThat(response.data().totalElements()).isEqualTo(2),
                    () -> assertThat(productInfos).extracting(ProductInfo::productId)
                            .containsExactly(secondProduct.getId(), firstProduct.getId())
            );
        }

        @DisplayName("비정규화 상품 목록 조회 시 성공")
        @Test
        void find_products_denormalization_test() {
            Member member = prepareMember();
            Brand brand = prepareBrand();
            Product product = prepareProduct(brand);

            ParameterizedTypeReference<ApiResponse<ProductInfoPageResponse>> responseType =
                    new ParameterizedTypeReference<>() {};
            EntityExchangeResult<ApiResponse<ProductInfoPageResponse>> result = restTestClient.get()
                    .uri(ENDPOINT_GET + "/denormalization?page=0&brandIds=" + brand.getId() + "&size=2&sort=latestAt")
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<ProductInfoPageResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();
            ProductInfo productInfo = response.data().content().getFirst();

            assertAll(
                    () -> assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS),
                    () -> assertThat(response.data().totalElements()).isOne(),
                    () -> assertThat(productInfo.productId()).isEqualTo(product.getId()),
                    () -> assertThat(productInfo.productName()).isEqualTo(product.getName()),
                    () -> assertThat(productInfo.brandId()).isEqualTo(brand.getId())
            );
        }

        @DisplayName("Redis 상품 목록 조회 시 성공")
        @Test
        void find_products_with_redis_test() {
            Member member = prepareMember();
            Brand brand = prepareBrand();
            Product product = prepareProduct(brand);

            ParameterizedTypeReference<ApiResponse<ProductInfoPageResponse>> responseType =
                    new ParameterizedTypeReference<>() {};
            EntityExchangeResult<ApiResponse<ProductInfoPageResponse>> result = restTestClient.get()
                    .uri(ENDPOINT_GET + "/redis?page=0&brandIds=" + brand.getId() + "&size=2&sort=latestAt")
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<ProductInfoPageResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();
            ProductInfo productInfo = response.data().content().getFirst();

            assertAll(
                    () -> assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS),
                    () -> assertThat(response.data().totalElements()).isOne(),
                    () -> assertThat(productInfo.productId()).isEqualTo(product.getId()),
                    () -> assertThat(productInfo.productName()).isEqualTo(product.getName()),
                    () -> assertThat(productInfo.brandId()).isEqualTo(brand.getId())
            );
        }
    }
}
