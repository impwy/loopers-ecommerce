package com.loopers.application;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.required.MemberRepository;
import com.loopers.domain.member.Member;
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
}
