package com.loopers.domain.member;

public record MemberCreate(
        String memberId,
        String password,
        Gender gender,
        String email,
        String birthDay
) {
}
