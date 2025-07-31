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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "member")
@Getter
@ToString
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

    @JoinColumn(name = "point_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Point point;

    private Member(MemberId memberId, String passwordHash, Gender gender, Email email, LocalDate birthday, Point point) {
        this.memberId = memberId;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.email = email;
        this.birthday = birthday;
        this.point = point;
    }

    public static Member create(CreateMemberSpec createMemberSpec) {
        return new Member(
                new MemberId(createMemberSpec.memberId()),
                requireNonNull(createMemberSpec.password()),
                requireNonNull(createMemberSpec.gender()),
                new Email(createMemberSpec.email()),
                createMemberSpec.birthday(),
                Point.create()
        );
    }

    public BigDecimal charge(BigDecimal amount) {
        return this.point.charge(amount);
    }

    public BigDecimal decrease(BigDecimal amount) {
        return this.point.decrease(amount);
    }
}


