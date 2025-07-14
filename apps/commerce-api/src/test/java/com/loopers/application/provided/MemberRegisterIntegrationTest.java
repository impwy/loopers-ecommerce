package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.MemberModifyService;
import com.loopers.application.required.MemberRepository;
import com.loopers.domain.DuplicateMemberIdException;
import com.loopers.domain.Gender;
import com.loopers.domain.Member;
import com.loopers.domain.MemberFixture;
import com.loopers.domain.MemberRegisterRequest;
import com.loopers.infrastructure.MemberJpaRepository;
import com.loopers.utils.DatabaseCleanUp;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
class MemberRegisterIntegrationTest {
    @Autowired
    MemberRegister memberRegister;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @MockitoSpyBean
    MemberJpaRepository memberJpaRepository;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Test
    void register() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();
        assertThat(member.getMemberId().memberId()).isEqualTo("pwy6817");
    }

    @Test
    void registerWithSpy() {
        Member member = Member.register(new MemberRegisterRequest("pwy6817", "secret", Gender.MALE, "pwy6817@loopers.app", "2025-07-13"));
        memberJpaRepository.save(member);

        verify(memberJpaRepository, times(1)).save(member);
    }

    @Test
    void registerTestWithMockito() {
        MemberRepository memberRepositoryMock = Mockito.mock(MemberRepository.class);

        MemberRegister memberRegister = new MemberModifyService(memberRepositoryMock);

        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();

        verify(memberRepositoryMock, times(1)).save(member);
    }

    @Test
    void throwDuplicateMemberIdException_whenMemberId_duplicated() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
                .isInstanceOf(DuplicateMemberIdException.class);
    }
}
