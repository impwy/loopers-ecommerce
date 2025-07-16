package com.loopers.domain.member;

import java.math.BigDecimal;

import com.loopers.domain.BaseEntity;

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
        return this.amount.add(amount);
    }
}
