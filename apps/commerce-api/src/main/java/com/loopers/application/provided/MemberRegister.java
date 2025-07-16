package com.loopers.application.provided;

import java.math.BigDecimal;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberRegisterRequest;

import jakarta.validation.Valid;

/**
 * 회원 등록 기능을 제공한다
 */
public interface MemberRegister {
    Member register(@Valid MemberRegisterRequest registerRequest);

    BigDecimal chargePoint(Long memberId, BigDecimal amount);
}
