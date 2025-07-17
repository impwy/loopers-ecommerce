package com.loopers.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.MemberRegister;
import com.loopers.domain.member.Member;
import com.loopers.interfaces.api.member.dto.MemberRegisterRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberFacade {
    private final MemberFinder memberFinder;
    private final MemberRegister memberRegister;

    public Member find(Long memberId) {
        return memberFinder.find(memberId);
    }

    public Member register(MemberRegisterRequest registerRequest) {
        return memberRegister.register(registerRequest);
    }

    public BigDecimal chargePoint(Long memberId, BigDecimal amount) {
        return memberRegister.chargePoint(memberId, amount);
    }
}
