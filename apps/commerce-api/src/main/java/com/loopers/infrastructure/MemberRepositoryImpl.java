package com.loopers.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.loopers.application.required.MemberRepository;
import com.loopers.domain.Member;

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
    public Optional<Member> findByMemberid(String memberId) {
        Optional<Member> optionalMember = memberJpaRepository.findByMemberid(memberId);
        return optionalMember;
    }
}
