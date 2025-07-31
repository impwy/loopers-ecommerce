package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.support.error.CoreException;

class PointChargeTest {
    @DisplayName("1 이상의 정수로 포인트를 충전 시 성공한다.")
    @ParameterizedTest
    @ValueSource(ints = { 1, 10, 100})
    void successWhenChargePointInvalid(int chargePoint) {
        Member member = MemberFixture.createMember();
        BigDecimal amount = member.charge(BigDecimal.valueOf(chargePoint));
        assertThat(amount).isEqualTo(BigDecimal.valueOf(chargePoint));
    }

    @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
    @ParameterizedTest
    @ValueSource(ints = { -1, 0})
    void failWhenChargePointNotInvalid(int chargePoint) {
        Member member = MemberFixture.createMember();
        assertThatThrownBy(() -> member.getPoint().charge(BigDecimal.valueOf(chargePoint)))
            .isInstanceOf(CoreException.class);
    }
}
