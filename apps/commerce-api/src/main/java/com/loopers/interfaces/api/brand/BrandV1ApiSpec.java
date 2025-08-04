package com.loopers.interfaces.api.brand;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.brand.dto.BrandV1Dto.Response.BrandInfoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 브랜드 관련 API 스펙입니다.
 */
@Tag(name = "Brand V1 API", description = "Brand API 입니다.")
public interface BrandV1ApiSpec {
    @Operation(
            summary = "브랜드 조회",
            description = "브랜드 아이디로 브랜드와 상품을 조회합니다"
    )
    ApiResponse<BrandInfoResponse> find(
            @Schema(name = "브랜드 ID", description = "브랜드 ID")
            Long brandId
    );
}
