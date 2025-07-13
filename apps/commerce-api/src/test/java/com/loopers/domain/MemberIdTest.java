package com.loopers.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MemberIdTest {
    @Test
    void equalityTest() {
        var memberId1 = new MemberId("pwy6817");
        var memberId2 = new MemberId("pwy6817");

        assertThat(memberId1).isEqualTo(memberId2);
    }
}
