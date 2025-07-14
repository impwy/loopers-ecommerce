package com.loopers.domain.member;

import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;

@Embeddable
public record MemberId(String memberId) {
    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,10}$");

    public MemberId {
        if (!MEMBER_ID_PATTERN.matcher(memberId).matches()) {
            throw new IllegalArgumentException("ID는 영문 및 숫자 10자 이내로 만들어야 합니다: " + memberId);
        }
    }
}
