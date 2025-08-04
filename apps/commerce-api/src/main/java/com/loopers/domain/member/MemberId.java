package com.loopers.domain.member;

import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class MemberId {
    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{1,10}$");
    private String memberId;

    public MemberId(String memberId) {
        if (!MEMBER_ID_PATTERN.matcher(memberId).matches()) {
            throw new IllegalArgumentException("ID는 영문 및 숫자 10자 이내로 만들어야 합니다: " + memberId);
        }
        this.memberId = memberId;
    }

    public String memberId() {return memberId;}
}
