package com.loopers.domain.member;

import static org.springframework.util.Assert.state;

import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;

@Embeddable
public record UserId(String value) {
    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{1,10}$");

    public UserId {
        state(MEMBER_ID_PATTERN.matcher(value).matches(), "ID는 영문 및 숫자 10자 이내로 만들어야 합니다: " + value);
    }
}
