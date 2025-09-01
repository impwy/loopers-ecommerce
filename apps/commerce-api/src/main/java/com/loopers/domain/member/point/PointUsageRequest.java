package com.loopers.domain.member.point;

import java.math.BigDecimal;

import com.loopers.domain.member.MemberId;

public record PointUsageRequest(MemberId memberId, BigDecimal totalAmount) {
}
