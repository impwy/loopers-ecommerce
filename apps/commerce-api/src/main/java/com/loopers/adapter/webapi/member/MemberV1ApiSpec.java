package com.loopers.adapter.webapi.member;

import java.math.BigDecimal;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.member.dto.MemberV1Dto;
import com.loopers.application.member.MemberRegisterRequest;
import com.loopers.domain.member.UserId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Member 관련 API 입니다.
 */
@Tag(name = "Member V1 API", description = "Member API 입니다.")
public interface MemberV1ApiSpec {
    @Operation(
            summary = "회원생성",
            description = "회원을 생성합니다"
    )
    ApiResponse<MemberV1Dto.MemberRegisterResponse> register(
            @Schema(name = "회원 생성용 DTO", description = "생성할 회원 정보")
            MemberRegisterRequest registerRequest);

    @Operation(
            summary = "회원 조회",
            description = "회원을 조회합니다"
    )
    ApiResponse<MemberV1Dto.MemberInfoResponse> find(
            @Schema(name = "회원 ID", description = "조회할 회원 ID")
            UserId userId);

    @Operation(
            summary = "회원 포인트 조회",
            description = "회원의 포인트를 조회합니다"
    )
    ApiResponse<MemberV1Dto.MemberWithPointResponse> findPoints(
            @Schema(name = "회원 ID", description = "조회할 회원 ID")
            UserId userId);

    @Operation(
            summary = "회원 포인트 충전",
            description = "회원의 포인트를 충전합니다"
    )
    ApiResponse<MemberV1Dto.MemberWithPointResponse> chargePoints(
            @Schema(name = "회원 ID", description = "충전할 회원 ID")
            UserId userId,
            @Schema(name = "충전할 포인트", description = "충전할 포인트")
            BigDecimal chargePoint);
}
