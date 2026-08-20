package com.loopers.domain.member;

import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class UserId {
    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{1,10}$");
    private String value;

    public UserId(String value) {
        if (!MEMBER_ID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("ID는 영문 및 숫자 10자 이내로 만들어야 합니다: " + value);
        }
        this.value = value;
    }

    public String userId() {return value;}
}
