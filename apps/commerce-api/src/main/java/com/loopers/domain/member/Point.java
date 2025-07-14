package com.loopers.domain.member;

import java.math.BigInteger;

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
    private BigInteger value;

    public static Point create() {
        Point point = new Point();
        point.value = BigInteger.ZERO;
        return point;
    }
}
