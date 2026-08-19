package com.loopers.application.member;

import org.springframework.stereotype.Service;

import com.loopers.application.member.provided.MemberFinder;
import com.loopers.application.member.required.MemberRepository;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.member.MemberNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberQueryService implements MemberFinder {
    private final MemberRepository memberRepository;

    @Override
    public Member find(Long memberId) {
        return memberRepository.find(memberId)
                               .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다: " + memberId));
    }

    @Override
    public Member findByMemberId(MemberId memberId) {
        return memberRepository.findWithPoint(memberId)
                               .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다: " + memberId));
    }

    @Override
    public Member findByMemberIdWithPessimisticLock(MemberId memberId) {
        return memberRepository.findByMemberIdWithPessimisticLock(memberId)
                               .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다: " + memberId));
    }
}
