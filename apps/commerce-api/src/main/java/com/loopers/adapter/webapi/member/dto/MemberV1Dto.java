package com.loopers.adapter.webapi.member.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.loopers.domain.member.Gender;
import com.loopers.domain.member.Member;

public class MemberV1Dto {
    public record MemberInfoResponse(Long id, String memberId, String email, Gender gender, LocalDate birthday) {
        public static MemberInfoResponse of(Member member) {
            return new MemberInfoResponse(member.getId(), member.getUserId().value(), member.getEmail().email(),
                                          member.getGender(), member.getBirthday());
        }
    }

    public record MemberRegisterResponse(Long id, String userId, String emailAddress, String gender, LocalDate birthday) {
        public static MemberRegisterResponse of(Member member) {
            return new MemberRegisterResponse(member.getId(), member.getUserId().value(), member.getEmail().email(),
                                              member.getGender().name(), member.getBirthday());
        }
    }

    public record MemberWithPointResponse(Long id, String userId, BigDecimal amount) {
        public static MemberWithPointResponse of(Member member) {
            return new MemberWithPointResponse(member.getId(), member.getUserId().value(), member.getPoint());
        }
    }
}
