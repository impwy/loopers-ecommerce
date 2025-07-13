package com.loopers.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class MemberTest {
    @Test
    void createMemberTest() {
        var member = new Member(new MemberId("pwy6817"), "secret", Gender.MALE, new Email("pwy6817@loopers.app"), new Birthday("2025-07-13"));

        assertAll(
                () -> assertThat(member.getId()).isNotNull(),
                () -> assertThat(member.getMemberId().memberId()).isEqualTo("pwy6817"),
                () -> assertThat(member.getPasswordHash()).isEqualTo("secret"),
                () -> assertThat(member.getGender()).isEqualTo(Gender.MALE),
                () -> assertThat(member.getEmail().email()).isEqualTo("pwy6817@loopers.app"),
                () -> assertThat(member.getBirthday().birthday()).isEqualTo("2025-07-13")
        );
    }

    @Test
    void throwIllegalArgumentException_whenMemberId_notMeths() {
        assertThatThrownBy(() -> new Member(new MemberId("invalid_memberId"), "secret", Gender.MALE, new Email("pwy6817@loopers.app"), new Birthday("2025-07-13")))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwIllegalArgumentException_whenEmail_notMeths() {
        assertThatThrownBy(() -> new Member(new MemberId("pwy6817"), "secret", Gender.MALE, new Email("invalid_email"), new Birthday("2025-07-13")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwIllegalArgumentException_whenBirthday_notMeths() {
        assertThatThrownBy(() -> new Member(new MemberId("pwy6817"), "secret", Gender.MALE, new Email("pwy6817@loopers.app"), new Birthday("invalid_birthday")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
