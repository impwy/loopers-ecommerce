package com.loopers.application.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.provided.CouponRegister;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.interfaces.api.coupon.dto.CouponV1Dto.Request.CreateCouponRequest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

@SpringBootTest
class CouponRegisterIntegrationTest {

    @MockitoSpyBean
    private CouponRegister couponRegister;

    @DisplayName("code가 null일 때 쿠폰생성 실패")
    @Test
    void fail_createCoupon_when_code_is_null() {
        CoreException coreException = assertThrows(CoreException.class,
                                                   () -> new CreateCouponRequest(null, 1L, DiscountPolicy.AMOUNT, CouponType.MEMBER));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("코드는 필수 입니다.");
    }

    @DisplayName("DiscountPolicy가 null일 때 쿠폰생성 실패")
    @Test
    void fail_createCoupon_when_discountPolicy_is_null() {
        CoreException coreException = assertThrows(CoreException.class,
                                                   () -> new CreateCouponRequest("coupon", 1L, null, CouponType.MEMBER));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("할인 정책은 필수 입니다.");
    }

    @DisplayName("CouponType이 null일 때 쿠폰생성 실패")
    @Test
    void fail_createCoupon_when_couponType_is_null() {
        CoreException coreException = assertThrows(CoreException.class,
                                                   () -> new CreateCouponRequest("coupon", 1L, DiscountPolicy.AMOUNT, null));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("쿠폰 타입은 필수 입니다.");
    }

    @DisplayName("쿠폰 생성 통합 테스트")
    @Test
    void createCouponTest() {
        CreateCouponSpec couponSpec = CouponFixture.createCouponSpec();
        Coupon coupon = couponRegister.create(couponSpec);

        assertThat(coupon.getCouponType()).isEqualTo(couponSpec.couponType());
        assertThat(coupon.getCode()).isEqualTo(couponSpec.code());
    }
}
