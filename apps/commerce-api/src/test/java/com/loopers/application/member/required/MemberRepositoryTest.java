package com.loopers.application.member.required;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.member.UserId;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class MemberRepositoryTest {
    final MemberRepository memberRepository;
    final EntityManager entityManager;

    @Test
    void saveAndFindById() {
        Member member = memberRepository.save(MemberFixture.createMember());
        entityManager.flush();
        entityManager.clear();

        Member found = memberRepository.findById(member.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(member.getId());
        assertThat(found.getUserId()).isEqualTo(member.getUserId());
    }

    @Test
    void findByMemberId() {
        Member member = memberRepository.save(MemberFixture.createMember());
        UserId userId = new UserId(member.getUserId().userId());
        entityManager.flush();
        entityManager.clear();

        Member found = memberRepository.findByUserId(userId).orElseThrow();

        assertThat(found.getId()).isEqualTo(member.getId());
    }

    @Test
    void findWithPoint() {
        Member member = memberRepository.save(MemberFixture.createMember());
        UserId userId = new UserId(member.getUserId().userId());
        entityManager.flush();
        entityManager.clear();

        Member found = memberRepository.findWithPoint(userId).orElseThrow();

        assertThat(found.getPoint()).isNotNull();
    }

    @Test
    void findByMemberIdWithPessimisticLock() {
        Member member = memberRepository.save(MemberFixture.createMember());
        UserId userId = new UserId(member.getUserId().userId());
        entityManager.flush();
        entityManager.clear();

        Member found = memberRepository.findByUserIdWithPessimisticLock(userId).orElseThrow();

        assertThat(found.getId()).isEqualTo(member.getId());
    }
}
