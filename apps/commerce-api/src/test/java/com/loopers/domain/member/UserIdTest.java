package com.loopers.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserIdTest {
    @DisplayName("MemberId 객체를 테스트한다.")
    @Test
    void equalityTest() {
        var memberId1 = new UserId("pwy6817");
        var memberId2 = new UserId("pwy6817");

        assertThat(memberId1).isEqualTo(memberId2);
    }
}
