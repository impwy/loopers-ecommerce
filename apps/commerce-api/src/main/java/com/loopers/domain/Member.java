package com.loopers.domain;

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
    private MemberId memberid;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Email email;

    @Embedded
    private Birthday birthday;

    public Member(MemberId memberid, String passwordHash, Gender gender, Email email, Birthday birthday) {
        this.memberid = memberid;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.email = email;
        this.birthday = birthday;
    }
}


