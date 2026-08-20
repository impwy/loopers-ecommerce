package com.loopers.application.member;

import org.springframework.stereotype.Service;

import com.loopers.application.member.provided.MemberFinder;
import com.loopers.application.member.required.MemberRepository;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberNotFoundException;
import com.loopers.domain.member.UserId;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberQueryService implements MemberFinder {
    private final MemberRepository memberRepository;

    @Override
    public Member find(Long memberId) {
        return memberRepository.findById(memberId)
                               .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다: " + memberId));
    }

    @Override
    public Member findByUserId(UserId userId) {
        return memberRepository.findByUserId(userId)
                               .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다: " + userId));
    }

    @Override
    public Member findWithPoint(UserId userId) {
        return memberRepository.findWithPoint(userId)
                               .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다: " + userId));
    }

    @Override
    public Member findByMemberIdWithPessimisticLock(UserId userId) {
        return memberRepository.findByUserIdWithPessimisticLock(userId)
                               .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다: " + userId));
    }
}
