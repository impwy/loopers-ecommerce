package com.loopers.interfaces.api.member.dto;

import com.loopers.domain.Member;

public record MemberRegisterResponse(Long id, String memberId, String emailAddress, String gender, String birthday) {
    public static MemberRegisterResponse of(Member member) {
        return new MemberRegisterResponse(member.getId(), member.getMemberId().memberId(), member.getEmail().email(),
                                          member.getGender().name(), member.getBirthday().birthday());
    }
}
