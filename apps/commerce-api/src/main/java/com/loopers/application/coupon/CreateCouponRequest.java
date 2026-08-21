package com.loopers.application.coupon;

import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;

import jakarta.validation.constraints.NotNull;

public record CreateCouponRequest(@NotNull String code,
                                  @NotNull Long quantity,
                                  @NotNull DiscountPolicy discountPolicy,
                                  @NotNull CouponType couponType) {
    public CreateCouponRequest {
        if (code == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "코드는 필수 입니다.");
        }
        if (discountPolicy == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 정책은 필수 입니다.");
        }
        if (couponType == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 타입은 필수 입니다.");
        }
    }

    public static CreateCouponRequest create(String code, Long quantity,
                                             DiscountPolicy discountPolicy, CouponType couponType) {
        return new CreateCouponRequest(code, quantity, discountPolicy, couponType);
    }

    public CreateCouponSpec toCreateCouponSpec() {
        return new CreateCouponSpec(code, quantity, discountPolicy, couponType);
    }
}
