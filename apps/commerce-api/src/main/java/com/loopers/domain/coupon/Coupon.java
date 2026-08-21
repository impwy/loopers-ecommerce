package com.loopers.domain.coupon;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.state;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.couponusage.CouponUsage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
    private CouponType type;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @Column(name = "expired_date")
    private LocalDate expiredDate;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponUsage> couponUsages = new ArrayList<>();

    public static Coupon create(CreateCouponSpec createCouponSpec) {
        state(createCouponSpec.quantity() > 0, "쿠폰 수량은 0개 이상이여야 합니다.");

        Coupon coupon = new Coupon();
        coupon.code = requireNonNull(createCouponSpec.code());
        coupon.quantity = requireNonNull(createCouponSpec.quantity());
        coupon.discountPolicy = requireNonNull(createCouponSpec.discountPolicy());
        coupon.type = requireNonNull(createCouponSpec.couponType());
        coupon.status = CouponStatus.CREATED;

        return coupon;
    }

    public Coupon addMemberCoupon(CouponUsage couponUsage) {
        this.couponUsages.add(couponUsage);
        couponUsage.addCoupon(this);
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
