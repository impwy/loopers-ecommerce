package com.loopers.application;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.PointFinder;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.Point;

import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PointQueryService implements PointFinder {
    private final MemberFinder memberFinder;

    @Override
    public Point find(Long memberId) {
        Member member = memberFinder.find(memberId);
        return member.getPoint();
    }
}
