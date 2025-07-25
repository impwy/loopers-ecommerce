package com.loopers.interfaces.api.point;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.MemberFacade;
import com.loopers.application.PointFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto.Response.PointAmountResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointV1ApiController implements PointV1ApiSpec {
    private final MemberFacade memberFacade;
    private final PointFacade pointFacade;

    @Override
    @GetMapping("/{memberId}")
    public ApiResponse<PointAmountResponse> find(@PathVariable Long memberId) {
        BigDecimal points = pointFacade.getPoints(memberId);
        return ApiResponse.success(PointV1Dto.Response.PointAmountResponse.of(points));
    }

    @Override
    @PostMapping("/charge/{memberId}")
    public ApiResponse<PointAmountResponse> charge(@PathVariable("memberId") Long memberId,
                                                   @RequestBody BigDecimal amount) {
        BigDecimal chargedPoint = memberFacade.chargePoint(memberId, amount);
        return ApiResponse.success(PointV1Dto.Response.PointAmountResponse.of(chargedPoint));
    }
}
