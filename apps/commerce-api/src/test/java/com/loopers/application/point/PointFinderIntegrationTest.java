package com.loopers.application.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.PointFinder;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.member.MemberNotFoundException;
import com.loopers.domain.member.point.Point;
import com.loopers.infrastructure.member.MemberJpaRepository;
import com.loopers.utils.DatabaseCleanUp;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@RequiredArgsConstructor
public class PointFinderIntegrationTest {

    @Autowired
    PointFinder pointFinder;

    @MockitoSpyBean
    MemberJpaRepository memberJpaRepository;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Transactional
    @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
    @Test
    void returnPoint_whenMemberIdIsExist() {
        Member member = memberJpaRepository.save(MemberFixture.createMember());

        Point result = pointFinder.find(member.getMemberId());

        assertThat(result.getAmount()).isNotNull();
        assertThat(result.getAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Transactional
    @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, 예외가 발생한다.")
    @Test
    void throwException_whenMemberIdIsNotExist() {
        assertThatThrownBy(() -> pointFinder.find(new MemberId("missing1")))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
