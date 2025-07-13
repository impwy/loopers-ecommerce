package com.loopers.application.provided;

import static com.loopers.domain.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.domain.Birthday;
import com.loopers.domain.DuplicateMemberIdException;
import com.loopers.domain.Email;
import com.loopers.domain.Member;
import com.loopers.domain.MemberId;
import com.loopers.domain.MemberRegisterRequest;
import com.loopers.infrastructure.MemberJpaRepository;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
public class MemberRegisterIntegrationTest {
    @Autowired
    MemberRegister memberRegister;

    @MockitoSpyBean
    MemberJpaRepository memberJpaRepository;

    @Test
    void register() {
        Member member = memberRegister.register(new MemberRegisterRequest("pwy6817", "secret", MALE, "pwy6817@loopers.app", "2025-07-13"));

        assertThat(member.getId()).isNotNull();
        assertThat(member.getMemberId().memberId()).isEqualTo("pwy6817");
    }

    @Test
    void registerWithSpy() {
        Member member = new Member(new MemberId("pwy6817"), "secret", MALE, new Email("pwy6817@loopers.app"), new Birthday("2025-07-13"));
        memberJpaRepository.save(member);

        verify(memberJpaRepository, times(1)).save(member);
    }

    @Test
    void DuplicateMemberIdException_whenMemberId_duplicated() {
        Member member = memberRegister.register(new MemberRegisterRequest("pwy6817", "secret", MALE, "pwy6817@loopers.app", "2025-07-13"));

        assertThatThrownBy(() -> memberRegister.register(new MemberRegisterRequest("pwy6817", "secret", MALE, "pwy6817@loopers.app", "2025-07-13")))
                .isInstanceOf(DuplicateMemberIdException.class);
    }
}
