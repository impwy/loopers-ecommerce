package com.loopers.domain;

import static java.util.Objects.requireNonNull;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "member")
@Getter
@ToString
@NaturalIdCache
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Embedded
    @NaturalId
    private MemberId memberId;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Email email;

    @Embedded
    private Birthday birthday;

    public Member(MemberId memberId, String passwordHash, Gender gender, Email email, Birthday birthday) {
        this.memberId = memberId;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.email = email;
        this.birthday = birthday;
    }

    public static Member register(MemberRegisterRequest registerRequest) {
        Member member = new Member();
        member.memberId = new MemberId(registerRequest.memberId());
        member.passwordHash = requireNonNull(registerRequest.password());
        member.gender = requireNonNull(registerRequest.gender());
        member.email = new Email(registerRequest.email());
        member.birthday = new Birthday(registerRequest.birthDay());

        return member;
    }
}


