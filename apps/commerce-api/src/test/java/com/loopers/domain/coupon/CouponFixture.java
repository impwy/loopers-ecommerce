package com.loopers.domain.coupon;

import java.util.ArrayList;

import com.loopers.application.coupon.CreateCouponRequest;

import org.instancio.Instancio;

import static org.instancio.Select.field;

public class CouponFixture {
    public static Coupon createCoupon() {
        return Instancio.of(Coupon.class)
                        .ignore(field(Coupon::getId))
                        .generate(field(Coupon::getCode), gen -> gen.string().alphaNumeric().minLength(1).maxLength(20))
                        .generate(field(Coupon::getQuantity), gen -> gen.longs().range(1L, 100L))
                        .generate(field(Coupon::getDiscountPolicy), gen -> gen.enumOf(DiscountPolicy.class))
                        .generate(field(Coupon::getType), gen -> gen.enumOf(CouponType.class))
                        .set(field(Coupon::getCouponUsages), new ArrayList<>())
                        .create();
    }

    public static CreateCouponSpec createCouponSpec() {
        return Instancio.of(CreateCouponSpec.class)
                        .generate(field(CreateCouponSpec::code), gen -> gen.string().alphaNumeric().minLength(1).maxLength(20))
                        .generate(field(CreateCouponSpec::quantity), gen -> gen.longs().range(1L, 100L))
                        .generate(field(CreateCouponSpec::discountPolicy), gen -> gen.enumOf(DiscountPolicy.class))
                        .generate(field(CreateCouponSpec::couponType), gen -> gen.enumOf(CouponType.class))
                        .create();
    }

    public static CreateCouponSpec createCouponSpec(String code, Long quantity,
                                                    DiscountPolicy discountPolicy, CouponType couponType) {
        return Instancio.of(CreateCouponSpec.class)
                        .set(field(CreateCouponSpec::code), code)
                        .set(field(CreateCouponSpec::quantity), quantity)
                        .set(field(CreateCouponSpec::discountPolicy), discountPolicy)
                        .set(field(CreateCouponSpec::couponType), couponType)
                        .create();
    }

    public static CreateCouponRequest createCouponRequest() {
        return Instancio.of(CreateCouponRequest.class)
                        .generate(field(CreateCouponRequest::code), gen -> gen.string().alphaNumeric().minLength(1).maxLength(20))
                        .generate(field(CreateCouponRequest::quantity), gen -> gen.longs().range(1L, 100L))
                        .generate(field(CreateCouponRequest::discountPolicy), gen -> gen.enumOf(DiscountPolicy.class))
                        .generate(field(CreateCouponRequest::couponType), gen -> gen.enumOf(CouponType.class))
                        .create();
    }

    public static CreateCouponRequest createCouponRequest(String code, Long quantity,
                                                          DiscountPolicy discountPolicy, CouponType couponType) {
        return Instancio.of(CreateCouponRequest.class)
                        .set(field(CreateCouponRequest::code), code)
                        .set(field(CreateCouponRequest::quantity), quantity)
                        .set(field(CreateCouponRequest::discountPolicy), discountPolicy)
                        .set(field(CreateCouponRequest::couponType), couponType)
                        .create();
    }
}
