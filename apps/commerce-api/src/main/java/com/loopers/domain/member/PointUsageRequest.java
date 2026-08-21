package com.loopers.domain.member;

import java.math.BigDecimal;

public record PointUsageRequest(UserId userId, BigDecimal totalAmount) {
}
