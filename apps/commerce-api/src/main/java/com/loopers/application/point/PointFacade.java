package com.loopers.application.point;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.point.dto.PointV1Dto.Response.PointAmountResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final PointQueryService pointQueryService;

    @Transactional
    public PointAmountResponse getPoints(MemberId memberId) {
        BigDecimal amount = pointQueryService.find(memberId).getAmount();
        return PointAmountResponse.of(amount);
    }
}
