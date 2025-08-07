package com.loopers.domain.coupon;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {
    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountPolicy discountPolicy;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @OneToMany(mappedBy = "coupon")
    private List<MemberCoupon> memberCoupons;

    private Coupon(String code, DiscountPolicy discountPolicy, CouponType couponType) {
        this.code = requireNonNull(code);
        this.discountPolicy = requireNonNull(discountPolicy);
        this.couponType = requireNonNull(couponType);
    }

    public static Coupon create(CreateCouponSpec createCouponSpec) {
        return new Coupon(createCouponSpec.code(),
                          createCouponSpec.discountPolicy(),
                          createCouponSpec.couponType());
    }

    public Coupon addMemberCoupon(MemberCoupon memberCoupon) {
        this.memberCoupons.add(memberCoupon);
        memberCoupon.addCoupon(this);
        return this;
    }
}
