package com.loopers.interfaces.api.member.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.loopers.domain.member.Gender;
import com.loopers.domain.member.MemberCreate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberRegisterRequest(
        @NotNull @Size(min = 1, max = 10) String memberId,
        @NotNull String password,
        @NotNull Gender gender,
        @NotNull @Email String email,
        @NotNull String birthday
) {
    private static final Pattern BIRTHDAY_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MemberCreate toMemberCreate() {
        checkBirthday();

        LocalDate formatedDate = LocalDate.parse(birthday, FORMATTER);
        return new MemberCreate(memberId, password, gender, email, formatedDate);
    }

    private void checkBirthday() {
        if (!BIRTHDAY_PATTERN.matcher(birthday).matches()) {
            throw new IllegalArgumentException("알맞은 생년월일 형식이 아닙니다: " + birthday);
        }
    }
}
