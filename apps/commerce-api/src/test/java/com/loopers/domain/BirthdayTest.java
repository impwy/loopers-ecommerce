package com.loopers.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class BirthdayTest {
    @Test
    void equalityTest() {
        var birthday1 = new Birthday("2025-07-13");
        var birthday2 = new Birthday("2025-07-13");

        assertThat(birthday1).isEqualTo(birthday2);
    }
}
