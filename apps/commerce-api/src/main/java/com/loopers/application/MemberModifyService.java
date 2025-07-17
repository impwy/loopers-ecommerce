package com.loopers.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.MemberRegister;
import com.loopers.application.required.MemberRepository;
import com.loopers.domain.member.DuplicateMemberIdException;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberCreate;
import com.loopers.interfaces.api.member.dto.MemberRegisterRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class MemberModifyService implements MemberRegister {
    private final MemberRepository memberRepository;
    private final MemberFinder memberFinder;

    @Override
    public Member register(MemberRegisterRequest registerRequest) {
        checkDuplicateId(registerRequest);
        MemberCreate memberCreate = registerRequest.toMemberCreate();
        Member member = Member.register(memberCreate);

        memberRepository.save(member);

        return member;
    }

    @Override
    public BigDecimal chargePoint(Long memberId, BigDecimal amount) {
        Member member = memberFinder.find(memberId);
        BigDecimal chargedPoint = member.charge(amount);
        memberRepository.save(member);
        return chargedPoint;
    }

    private void checkDuplicateId(MemberRegisterRequest registerRequest) {
        if (memberRepository.findByMemberId(registerRequest.memberId()).isPresent()) {
            throw new DuplicateMemberIdException("이미 사용중인 ID 입니다: " + registerRequest.memberId());
        }
    }
}
