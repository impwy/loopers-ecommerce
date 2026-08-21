package com.loopers.application.member.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.member.MemberModifyService;
import com.loopers.application.member.MemberRegisterRequest;
import com.loopers.application.member.required.MemberRepository;
import com.loopers.domain.member.DuplicateMemberIdException;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationValidServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationValidServiceTest
@RequiredArgsConstructor
class MemberRegisterTest extends BaseApplicationServiceTest {
    final MemberRegister memberRegister;

    @MockitoSpyBean
    MemberRepository memberRepository;

    @DisplayName("회원을 가입한다.")
    @Test
    void create() {
        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        Member member = memberRegister.register(request);

        assertThat(member.getId()).isNotNull();
        assertThat(member.getUserId().value()).isEqualTo(request.memberId());
    }

    @DisplayName("회원 가입시 User 저장이 수행된다. ( spy 검증 )")
    @Test
    void createWithSpy() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest("spyuser1"));

        verify(memberRepository, times(1)).save(member);
    }

    @DisplayName("회원 가입시 User 저장이 수행된다. ( mock )")
    @Test
    void createTestWithMockito() {
        MemberRepository memberRepositoryMock = Mockito.mock(MemberRepository.class);
        MemberFinder memberFinderMock = Mockito.mock(MemberFinder.class);

        MemberRegister memberRegister = new MemberModifyService(memberRepositoryMock, memberFinderMock);

        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member).isNotNull();

        verify(memberRepositoryMock, times(1)).save(member);
    }

    @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
    @Test
    void throwDuplicateMemberIdException_whenMemberId_duplicated() {
        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        memberRegister.register(request);

        assertThatThrownBy(() -> memberRegister.register(request))
                .isInstanceOf(DuplicateMemberIdException.class);
    }

    @DisplayName("회원이 포인트를 충전한다.")
    @Test
    void chargePoint() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        member = memberRegister.chargePoint(member.getUserId(), BigDecimal.TEN);

        assertThat(member.getPoint()).isEqualTo(BigDecimal.TEN);
    }
}
