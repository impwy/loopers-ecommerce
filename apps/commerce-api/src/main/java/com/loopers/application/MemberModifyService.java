package com.loopers.application;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.loopers.application.provided.MemberRegister;
import com.loopers.application.required.MemberRepository;
import com.loopers.domain.member.DuplicateMemberIdException;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberRegisterRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class MemberModifyService implements MemberRegister {
    private final MemberRepository memberRepository;

    @Override
    public Member register(MemberRegisterRequest registerRequest) {
        checkDuplicateId(registerRequest);
        Member member = Member.register(registerRequest);

        memberRepository.save(member);

        return member;
    }

    private void checkDuplicateId(MemberRegisterRequest registerRequest) {
        if (memberRepository.findByMemberId(registerRequest.memberId()).isPresent()) {
            throw new DuplicateMemberIdException("이미 사용중인 ID 입니다: " + registerRequest.memberId());
        }
    }
}
