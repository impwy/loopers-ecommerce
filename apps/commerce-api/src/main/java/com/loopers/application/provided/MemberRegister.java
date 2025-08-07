package com.loopers.application.provided;

import java.math.BigDecimal;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.Request.MemberRegisterRequest;

import jakarta.validation.Valid;

/**
 * 회원 등록 기능을 제공한다
 */
public interface MemberRegister {
    Member register(@Valid MemberRegisterRequest registerRequest);

    BigDecimal chargePoint(MemberId memberId, BigDecimal amount);

    Member usePoint(MemberId memberId, BigDecimal discountedPrice);
}
