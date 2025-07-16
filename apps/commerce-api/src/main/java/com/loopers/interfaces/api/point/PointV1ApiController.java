package com.loopers.interfaces.api.point;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.provided.MemberRegister;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.PointV1Dto;
import com.loopers.interfaces.api.member.dto.PointV1Dto.Response.PointAmountResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointV1ApiController {
    private final MemberRegister memberRegister;

    @PostMapping("/charge/{memberId}")
    public ApiResponse<PointAmountResponse> charge(@PathVariable("memberId") Long memberId,
                                                   @RequestBody String amount) {
        BigDecimal chargedPoint = memberRegister.chargePoint(memberId, amount);
        return ApiResponse.success(PointV1Dto.Response.PointAmountResponse.of(chargedPoint));
    }
}
