package com.loopers.domain.member;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "member")
@Getter
@ToString(callSuper = true, exclude = "point")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Embedded
    private MemberId memberId;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Email email;

    private LocalDate birthday;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Point point;

    public static Member register(MemberCreate memberCreate) {
        Member member = new Member();
        member.memberId = new MemberId(memberCreate.memberId());
        member.passwordHash = requireNonNull(memberCreate.password());
        member.gender = requireNonNull(memberCreate.gender());
        member.email = new Email(memberCreate.email());
        member.birthday = memberCreate.birthday();
        member.point = Point.create();

        return member;
    }

    public BigDecimal charge(BigDecimal amount) {
        return this.point.charge(amount);
    }
}


