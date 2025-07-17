package com.loopers.interfaces.api.point;

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
    String find(
            @Schema(name = "회원 ID", description = "조회할 회원 ID")
            String memberId
    );

    @Operation(
            summary = "포인트 충전",
            description = "회원의 포인트를 충전합니다"
    )
    String charge(
            @Schema(name = "회원 ID", description = "충전할 회원 ID")
            String memberId,
            @Schema(name = "POINT", description = "충전할 포인트")
            String amount);
}
