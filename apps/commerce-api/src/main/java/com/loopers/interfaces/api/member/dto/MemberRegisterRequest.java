package com.loopers.interfaces.api.member.dto;

import com.loopers.domain.member.Gender;
import com.loopers.domain.member.MemberCreate;

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
    public MemberCreate toMemberCreate() {
            return new MemberCreate(memberId, password, gender, email, birthDay);
    }
}
