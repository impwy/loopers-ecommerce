package com.loopers.application.member.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.member.required.MemberRepository;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.member.UserId;
import com.loopers.domain.member.MemberNotFoundException;
import com.loopers.support.stereotype.ApplicationServiceTest;
import com.loopers.utils.DatabaseCleanUp;

import lombok.RequiredArgsConstructor;

@ApplicationServiceTest
@RequiredArgsConstructor
class MemberFinderTest {
    final MemberFinder memberFinder;
    final DatabaseCleanUp databaseCleanUp;

    @MockitoSpyBean
    MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
    @Test
    void find_member_info() {
        Member member = memberRepository.save(MemberFixture.createMember());

        Member result = memberFinder.findWithPoint(member.getUserId());

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(member.getId()),
                () -> assertThat(result.getUserId().userId()).isEqualTo(member.getUserId().userId()),
                () -> assertThat(result.getEmail().email()).isEqualTo(member.getEmail().email()),
                () -> assertThat(result.getBirthday()).isEqualTo(member.getBirthday())
        );
    }

    @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, 예외가 발생한다.")
    @Test
    void find_member_fail() {
        assertThatThrownBy(() -> memberFinder.findWithPoint(new UserId("success0")))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }

    @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
    @Test
    void returnPoint_whenMemberIdIsExist() {
        Member member = memberRepository.save(MemberFixture.createMember());

        member = memberFinder.findWithPoint(member.getUserId());

        assertThat(member.getPoint()).isNotNull();
        assertThat(member.getPoint()).isEqualTo(BigDecimal.ZERO);
    }
}
