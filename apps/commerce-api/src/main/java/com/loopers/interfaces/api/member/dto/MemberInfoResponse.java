package com.loopers.interfaces.api.member.dto;

import com.loopers.domain.Gender;
import com.loopers.domain.Member;

public record MemberInfoResponse(
        Long id,
        String memberId,
        String email,
        Gender gender,
        String birthday
) {
    public static MemberInfoResponse of(Member member) {
        return new MemberInfoResponse(member.getId(), member.getMemberId().memberId(), member.getEmail().email(),
                                      member.getGender(), member.getBirthday().birthday());
    }
}
