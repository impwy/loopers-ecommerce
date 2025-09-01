package com.loopers.domain.coupon;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;

import jakarta.persistence.CascadeType;
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
    private Long quantity;

    @Enumerated(EnumType.STRING)
    private DiscountPolicy discountPolicy;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCoupon> memberCoupons = new ArrayList<>();

    private Coupon(String code, Long quantity, DiscountPolicy discountPolicy, CouponType couponType) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("쿠폰 수량은 0개 이상이여야 합니다." + quantity);
        }
        this.code = requireNonNull(code);
        this.quantity = requireNonNull(quantity);
        this.discountPolicy = requireNonNull(discountPolicy);
        this.couponType = requireNonNull(couponType);
    }

    public static Coupon create(CreateCouponSpec createCouponSpec) {
        return new Coupon(createCouponSpec.code(),
                          createCouponSpec.quantity(),
                          createCouponSpec.discountPolicy(),
                          createCouponSpec.couponType());
    }

    public Coupon addMemberCoupon(MemberCoupon memberCoupon) {
        this.memberCoupons.add(memberCoupon);
        memberCoupon.addCoupon(this);
        return this;
    }

    public Long useCoupon() {
        this.quantity -= 1;
        return quantity;
    }

    public void rollback() {
        this.quantity += 1;
    }
}
