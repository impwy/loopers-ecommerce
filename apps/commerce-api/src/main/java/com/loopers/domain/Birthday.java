package com.loopers.domain;

import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;

@Embeddable
public record Birthday(String birthday) {
    private static final Pattern BIRTHDAY_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");

    public Birthday {
        if (!BIRTHDAY_PATTERN.matcher(birthday).matches()) {
            throw new IllegalArgumentException("알맞은 생년월일 형식이 아닙니다: " + birthday);
        }
    }
}
