package com.loopers.infrastructure.member;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.loopers.application.required.MemberRepository;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        Member savedmember = memberJpaRepository.save(member);
        return savedmember;
    }

    @Override
    public Optional<Member> find(Long memberId) {
        return memberJpaRepository.findById(memberId);
    }

    @Override
    public Optional<Member> findByMemberId(MemberId memberId) {
        Optional<Member> optionalMember = memberJpaRepository.findByMemberId(memberId);
        return optionalMember;
    }

    @Override
    public Optional<Member> findWithPoint(MemberId memberId) {
        return memberJpaRepository.findWithPoint(memberId);
    }

    @Override
    public Optional<Member> findByMemberIdWithPessimisticLock(MemberId memberId) {
        return memberJpaRepository.findByMemberIdWithPessimisticLock(memberId);
    }
}
