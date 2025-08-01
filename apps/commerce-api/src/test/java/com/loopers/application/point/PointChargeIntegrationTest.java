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

import com.loopers.application.provided.MemberRegister;
import com.loopers.application.required.MemberRepository;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.member.MemberNotFoundException;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class PointChargeIntegrationTest {

    @Autowired
    private MemberRegister memberRegister;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
    @Test
    void failWhenChargePointNotExistedUser() {
        assertThatThrownBy(() -> memberRegister.chargePoint(new MemberId("fail0"), new BigDecimal(1000)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("충전 성공 테스트")
    @Test
    void charge_point_test() {
        Member member = MemberFixture.createMember();
        memberRepository.save(member);
        BigDecimal charge = new BigDecimal(1000);
        BigDecimal chargedPoint = memberRegister.chargePoint(member.getMemberId(), charge);

        assertThat(charge.compareTo(chargedPoint)).isZero();
    }
}
