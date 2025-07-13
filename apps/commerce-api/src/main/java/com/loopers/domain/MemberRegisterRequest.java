package com.loopers.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberRegisterRequest(
        @Size(min = 1, max = 10) String memberId,
        String password,
        @NotNull Gender gender,
        @Email String email,
        String birthDay
) {
}
