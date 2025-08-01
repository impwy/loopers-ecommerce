package com.loopers.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTest {
    @DisplayName("Email 객체를 테스트한다.")
    @Test
    void equalityTest() {
        var email1 = new Email("pwy@loopers.app");
        var email2 = new Email("pwy@loopers.app");

        assertThat(email1).isEqualTo(email2);
    }
}
