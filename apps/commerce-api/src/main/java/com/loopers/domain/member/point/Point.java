package com.loopers.domain.member.point;

import java.math.BigDecimal;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Table(name = "point")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {
    private BigDecimal amount;

    private Point(BigDecimal amount) {
        this.amount = amount;
    }

    public static Point create() {
        return new Point(BigDecimal.ZERO);
    }

    public BigDecimal charge(BigDecimal amount) {
        if (amount.intValue() <= 0) {
            throw new IllegalArgumentException("잘못된 충전 포인트입니다 : " + amount);
        }
        this.amount = this.amount.add(amount);
        return this.amount;
    }

    public BigDecimal usePoint(BigDecimal amount) {
        if (this.amount.intValue() <= 0 || this.amount.compareTo(amount) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.amount = this.amount.subtract(amount);
        return this.amount;
    }
}
