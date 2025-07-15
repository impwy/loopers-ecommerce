package com.loopers.interfaces.api.member.dto;

import com.loopers.domain.member.Gender;
import com.loopers.domain.member.Member;

public class MemberV1Dto {
    public record MemberInfoResponse(Long id, String memberId, String email, Gender gender, String birthday, String amount) {
        // TODO : Point 객체 반환을 완료해야 한다.
        public static MemberInfoResponse of(Member member) {
            return new MemberInfoResponse(member.getId(), member.getMemberId().memberId(), member.getEmail().email(),
                                          member.getGender(), member.getBirthday().birthday(), "0");
        }
    }

    public record MemberRegisterResponse(Long id, String memberId, String emailAddress, String gender, String birthday) {
        public static MemberRegisterResponse of(Member member) {
            return new MemberRegisterResponse(member.getId(), member.getMemberId().memberId(), member.getEmail().email(),
                                              member.getGender().name(), member.getBirthday().birthday());
        }
    }
}
