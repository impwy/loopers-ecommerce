package com.loopers.domain.couponusage;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponStatus;
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
import lombok.ToString;

@Entity
@Getter
@Table(name = "member_coupon")
@ToString(callSuper = true, exclude = {"member", "coupon"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponUsage extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    public static CouponUsage create(Member member, Coupon coupon) {
        CouponUsage couponUsage = new CouponUsage();
        couponUsage.member = member;
        couponUsage.coupon = coupon;
        couponUsage.status = CouponStatus.ACTIVE;
        return couponUsage;
    }

    public void addCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public void useCoupon() {
        this.status = CouponStatus.USED;
    }

    public void rollback() {
        this.status = CouponStatus.ACTIVE;
    }
}
