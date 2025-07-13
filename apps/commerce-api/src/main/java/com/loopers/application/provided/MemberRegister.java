package com.loopers.application.provided;

import com.loopers.domain.Member;
import com.loopers.domain.MemberRegisterRequest;

import jakarta.validation.Valid;

/**
 * 회원 등록 기능을 제공한다
 */
public interface MemberRegister {
    Member register(@Valid MemberRegisterRequest registerRequest);
}
