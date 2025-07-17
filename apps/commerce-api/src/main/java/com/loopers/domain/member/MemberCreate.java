package com.loopers.domain.member;

import java.time.LocalDate;

public record MemberCreate(
        String memberId,
        String password,
        Gender gender,
        String email,
        LocalDate birthday
) {
}
