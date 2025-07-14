package com.loopers.domain;

import static com.loopers.domain.member.Gender.MALE;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberRegisterRequest;

public class MemberFixture {
    public static Member createMember() {
        return Member.register(createMemberRegisterRequest());
    }

    public static MemberRegisterRequest createMemberRegisterRequest() {
        return new MemberRegisterRequest("pwy6817", "secret", MALE, "pwy6817@loopers.app", "2025-07-13");
    }
}
