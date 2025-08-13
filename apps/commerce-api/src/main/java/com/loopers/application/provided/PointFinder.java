package com.loopers.application.provided;

import com.loopers.domain.member.MemberId;
import com.loopers.domain.member.point.Point;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface PointFinder {
    Point find(@Valid @NotNull MemberId memberId);
}
