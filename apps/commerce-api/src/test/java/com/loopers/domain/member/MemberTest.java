package com.loopers.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

    @DisplayName("회원을 생성한다.")
    @Test
    void createMemberTest() {
        var member = Member.register(new MemberRegisterRequest("pwy6817", "secret", Gender.MALE, "pwy6817@loopers.app", "2025-07-13"));

        assertAll(
                () -> assertThat(member.getId()).isNotNull(),
                () -> assertThat(member.getMemberId().memberId()).isEqualTo("pwy6817"),
                () -> assertThat(member.getPasswordHash()).isEqualTo("secret"),
                () -> assertThat(member.getGender()).isEqualTo(Gender.MALE),
                () -> assertThat(member.getEmail().email()).isEqualTo("pwy6817@loopers.app"),
                () -> assertThat(member.getBirthday().birthday()).isEqualTo("2025-07-13")
        );
    }

    @DisplayName("ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = { "invalid_id", "abcdefghij", "abc12345678", "0123456789" })
    void throwIllegalArgumentException_whenMemberId_notMatch(String memberId) {
        assertThatThrownBy(() -> Member.register(new MemberRegisterRequest(memberId, "secret", Gender.MALE, "pwy6817@loopers.app", "2025-07-13")))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = { "invalid_email", "abc@abc", "abc.abc" })
    void throwIllegalArgumentException_whenEmail_notMatch(String email) {
        assertThatThrownBy(() -> Member.register(new MemberRegisterRequest("pwy6817", "secret", Gender.MALE, email, "2025-07-13")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = { "invalid_birthday", "20250707", "2025.07.07" })
    void throwIllegalArgumentException_whenBirthday_notMatch(String birthday) {
        assertThatThrownBy(() -> Member.register(new MemberRegisterRequest("pwy6817", "secret", Gender.MALE, "pwy6817@loopers.app", birthday)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
