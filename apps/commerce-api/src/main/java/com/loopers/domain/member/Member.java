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
@ToString(callSuper = true, exclude = "point")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Embedded
    private UserId userId;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Email email;

    private LocalDate birthday;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "point_id")
    private Point point;

    private Member(UserId userId, String passwordHash, Gender gender, Email email, LocalDate birthday, Point point) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.email = email;
        this.birthday = birthday;
        this.point = point;
    }

    public static Member create(CreateMemberSpec createMemberSpec) {
        return new Member(
                new UserId(createMemberSpec.memberId()),
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

    public BigDecimal usePoint(BigDecimal amount) {
        return this.point.usePoint(amount);
    }

    public BigDecimal getPoint() {
        return this.point.getAmount();
    }
}


