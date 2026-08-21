package com.loopers.application.member.provided;

import java.math.BigDecimal;

import com.loopers.application.member.MemberRegisterRequest;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.UserId;

import jakarta.validation.Valid;

/**
 * 회원 등록 기능을 제공한다
 */
public interface MemberRegister {
    Member register(@Valid MemberRegisterRequest registerRequest);

    Member chargePoint(UserId userId, BigDecimal amount);

    Member usePoint(UserId userId, BigDecimal discountedPrice);
}
