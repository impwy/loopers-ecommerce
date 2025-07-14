package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.loopers.application.required.MemberRepository;
import com.loopers.domain.Member;
import com.loopers.domain.MemberFixture;
import com.loopers.utils.DatabaseCleanUp;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
class MemberFinderIntegrationTest {
    @Autowired
    MemberFinder memberFinder;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Test
    void find_member_info() {
        Member member = memberRepository.save(MemberFixture.createMember());

        Member result = memberFinder.find(member.getId());

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(member.getId()),
                () -> assertThat(result.getMemberId().memberId()).isEqualTo(member.getMemberId().memberId()),
                () -> assertThat(result.getEmail().email()).isEqualTo(member.getEmail().email()),
                () -> assertThat(result.getBirthday().birthday()).isEqualTo(member.getBirthday().birthday())
        );
    }

    @Test
    void find_member_fail() {
        assertThatThrownBy(() -> memberFinder.find(999L))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
