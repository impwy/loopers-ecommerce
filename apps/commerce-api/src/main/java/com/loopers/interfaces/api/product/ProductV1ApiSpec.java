package com.loopers.interfaces.api.product;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.loopers.domain.product.ProductInfoWithRank;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Product 관련 API 입니다.
 */
@Tag(name = "Product V1 API", description = "Product API 입니다.")
public interface ProductV1ApiSpec {

    @Operation(
            summary = "상품 단건 조회",
            description = "상품을 단건 조회합니다"
    )
    ApiResponse<ProductInfoWithRank> getProductInfo(Long productId);

    @Operation(
            summary = "상품 목록 조회",
            description = "상품의 목록을 조회합니다"
    )
    ApiResponse<ProductInfoPageResponse> getProductsInfo(String sort,
                                                         List<Long> brandIds,
                                                         Pageable pageable);

    @Operation(
            summary = "상품 목록 조회",
            description = "상품의 목록을 조회합니다"
    )
    ApiResponse<ProductInfoPageResponse> getProductsInfoDenormalization(String sort,
                                                                        List<Long> brandIds,
                                                                        Pageable pageable);

    @Operation(
            summary = "상품 목록 조회",
            description = "상품의 목록을 조회합니다"
    )
    ApiResponse<ProductInfoPageResponse> getProductsInfoDenormalizationWithRedis(String sort,
                                                                                 List<Long> brandIds,
                                                                                 Pageable pageable);

    @Operation(
            summary = "상품 랭킹 조회",
            description = "상품의 랭킹을 조회합니다"
    )
    ApiResponse<ProductInfoPageResponse> getProductRanking(String date, Pageable pageable);
}
