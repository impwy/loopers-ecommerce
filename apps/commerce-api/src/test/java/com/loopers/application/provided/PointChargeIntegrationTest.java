package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.loopers.domain.member.MemberNotFoundException;

@SpringBootTest
public class PointChargeIntegrationTest {

    @Autowired
    MemberRegister memberRegister;

    @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
    @Test
    void failWhenChargePointNotExistedUser() {
        assertThatThrownBy(() -> memberRegister.chargePoint(999L, "1000"))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
