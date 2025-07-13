package com.loopers.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class EmailTest {
    @Test
    void equalityTest() {
        var email1 = new Email("pwy@loopers.app");
        var email2 = new Email("pwy@loopers.app");

        assertThat(email1).isEqualTo(email2);
    }
}
