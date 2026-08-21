package com.loopers.adapter.webapi.brand;

import org.springframework.data.domain.Pageable;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.brand.dto.BrandV1Dto.BrandCreateResponse;
import com.loopers.adapter.webapi.brand.dto.BrandV1Dto.BrandDetailResponse;
import com.loopers.application.brand.BrandCreateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 브랜드 관련 API 스펙입니다.
 */
@Tag(name = "Brand V1 API", description = "Brand API 입니다.")
public interface BrandV1ApiSpec {
    @Operation(
            summary = "브랜드 조회",
            description = "브랜드 아이디로 브랜드와 상품을 조회합니다"
    )
    ApiResponse<BrandDetailResponse> find(
            @Schema(name = "브랜드 ID", description = "브랜드 ID")
            Long brandId,
            Pageable pageable
    );

    @Operation(
            summary = "브랜드 생성",
            description = "브랜드 생성합니다"
    )
    ApiResponse<BrandCreateResponse> create(
            @Schema(name = "브랜드 요청", description = "브랜드 생성을 요청")
            @Valid BrandCreateRequest createRequest);

}
