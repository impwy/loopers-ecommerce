package com.loopers.application.member;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.MemberRegister;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.Request.MemberRegisterRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberFacade {
    private final MemberFinder memberFinder;
    private final MemberRegister memberRegister;

    public Member find(MemberId memberId) {
        return memberFinder.findByMemberId(memberId);
    }

    public Member register(MemberRegisterRequest registerRequest) {
        return memberRegister.register(registerRequest);
    }

    public BigDecimal getPoints(MemberId memberId) {
        return memberFinder.findByMemberId(memberId).getPoint().getAmount();
    }

    public BigDecimal chargePoint(MemberId memberId, BigDecimal amount) {
        return memberRegister.chargePoint(memberId, amount);
    }
}
