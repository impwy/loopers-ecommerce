package com.loopers.interfaces.api.point;

import java.math.BigDecimal;

import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto.Response.PointAmountResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Point 관련 API 입니다.
 */
@Tag(name = "Point V1 API", description = "Point API 입니다.")
public interface PointV1ApiSpec {
    @Operation(
            summary = "유저 조회",
            description = "유저 아이디로 회원을 조회합니다"
    )
    ApiResponse<PointAmountResponse> find(
            @Schema(name = "회원 ID", description = "조회할 회원 ID")
            MemberId memberId
    );

    @Operation(
            summary = "포인트 충전",
            description = "회원의 포인트를 충전합니다"
    )
    ApiResponse<PointAmountResponse> charge(
            @Schema(name = "회원 ID", description = "충전할 회원 ID")
            MemberId memberId,
            @Schema(name = "POINT", description = "충전할 포인트")
            BigDecimal amount);
}
