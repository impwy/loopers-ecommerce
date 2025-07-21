package com.loopers.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final PointQueryService pointQueryService;

    public BigDecimal getPoints(Long memberId) {
        return pointQueryService.find(memberId).getAmount();
    }
}
