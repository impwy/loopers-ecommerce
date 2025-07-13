package com.loopers.domain;

import static com.loopers.domain.Gender.MALE;

public class MemberFixture {
    public static MemberRegisterRequest createMemberRegisterRequest() {
        return new MemberRegisterRequest("pwy6817", "secret", MALE, "pwy6817@loopers.app", "2025-07-13");
    }
}
