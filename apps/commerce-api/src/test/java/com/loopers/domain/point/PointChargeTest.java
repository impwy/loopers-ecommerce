package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.support.error.CoreException;

class PointChargeTest {
    @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
    @Test
    void failWhenChargePointNotInvalid() {
        Member member = MemberFixture.createMember();
        assertThatThrownBy(() -> member.getPoint().charge(BigDecimal.valueOf(-1)))
            .isInstanceOf(CoreException.class);
    }
}
