package com.loopers.domain.coupon.membercoupon;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.member.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member_coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCoupon extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private MemberCoupon(Member member, Coupon coupon) {
        this.member = member;
        this.coupon = coupon;
        this.couponStatus = CouponStatus.CREATED;
    }

    public static MemberCoupon create(Member member, Coupon coupon) {
        return new MemberCoupon(member, coupon);
    }

    public void addCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public void useCoupon() {
        this.couponStatus = CouponStatus.USED;
    }

    public void rollback() {
        this.couponStatus = CouponStatus.CREATED;
    }
}
