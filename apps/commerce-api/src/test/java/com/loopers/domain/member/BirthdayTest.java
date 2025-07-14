package com.loopers.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BirthdayTest {
    @DisplayName("Birthday 객체를 테스트한다.")
    @Test
    void equalityTest() {
        var birthday1 = new Birthday("2025-07-13");
        var birthday2 = new Birthday("2025-07-13");

        assertThat(birthday1).isEqualTo(birthday2);
    }
}
