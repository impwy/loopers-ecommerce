package com.loopers.application.provided;

import com.loopers.domain.member.Point;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface PointFinder {
    Point find(@Valid @NotNull Long memberId);
}
