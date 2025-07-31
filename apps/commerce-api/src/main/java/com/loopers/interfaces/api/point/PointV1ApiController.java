package com.loopers.interfaces.api.point;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.member.MemberFacade;
import com.loopers.application.point.PointFacade;
import com.loopers.domain.member.MemberId;
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
    @GetMapping
    public ApiResponse<PointAmountResponse> find(MemberId memberId) {
        BigDecimal points = pointFacade.getPoints(memberId);
        return ApiResponse.success(PointV1Dto.Response.PointAmountResponse.of(points));
    }

    @Override
    @PostMapping("/charge")
    public ApiResponse<PointAmountResponse> charge(MemberId memberId,
                                                   @RequestBody BigDecimal amount) {
        BigDecimal chargedPoint = memberFacade.chargePoint(memberId, amount);
        return ApiResponse.success(PointV1Dto.Response.PointAmountResponse.of(chargedPoint));
    }
}
