package com.loopers.application.point;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.domain.member.MemberId;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final PointQueryService pointQueryService;

    @Transactional
    public BigDecimal getPoints(MemberId memberId) {
        return pointQueryService.find(memberId).getAmount();
    }
}
