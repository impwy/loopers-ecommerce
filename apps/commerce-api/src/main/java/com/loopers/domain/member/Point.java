package com.loopers.domain.member;

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
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {
    private BigDecimal amount;

    static Point create() {
        Point point = new Point();
        point.amount = BigDecimal.ZERO;
        return point;
    }

    public BigDecimal charge(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 충전 포인트입니다 : " + amount);
        }
        return this.amount.add(amount);
    }
}
