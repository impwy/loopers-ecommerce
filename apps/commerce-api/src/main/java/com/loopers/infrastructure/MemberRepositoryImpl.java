package com.loopers.infrastructure;

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
    public Optional<Member> findByMemberId(String memberId) {
        Optional<Member> optionalMember = memberJpaRepository.findByMemberId(new MemberId(memberId));
        return optionalMember;
    }

    @Override
    public Optional<Member> findById(Long memberId) {
        return memberJpaRepository.findWithPoint(memberId);
    }
}
