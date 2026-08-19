package com.loopers.adapter.webapi.rank;

import java.time.LocalDate;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface RankV1ApiSpec {

    @Operation(
            summary = "랭킹 조회",
            description = "일간, 주간, 월간 상품 랭킹을 조회한다."
    )
    ApiResponse<ProductInfoPageResponse> getProductRanking(
            @Parameter(
                    description = "조회 타입",
                    required = true,
                    example = "daily"
            )
            String period,
            @Parameter(
                    description = "조회할 날짜",
                    required = true,
                    example = "2021-01-01"
            )
            LocalDate date,
            Integer page, Integer size);
}
