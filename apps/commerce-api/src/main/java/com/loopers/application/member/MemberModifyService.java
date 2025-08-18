package com.loopers.application.member;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.MemberRegister;
import com.loopers.application.required.MemberRepository;
import com.loopers.domain.member.CreateMemberSpec;
import com.loopers.domain.member.DuplicateMemberIdException;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.Request.MemberRegisterRequest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

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
        CreateMemberSpec createMemberSpec = registerRequest.toMemberCreate();
        Member member = Member.create(createMemberSpec);

        memberRepository.save(member);

        return member;
    }

    @Override
    public BigDecimal chargePoint(MemberId memberId, BigDecimal amount) {
        Member member = memberFinder.findByMemberId(memberId);
        BigDecimal chargedPoint = member.charge(amount);
        memberRepository.save(member);
        return chargedPoint;
    }

    private void checkDuplicateId(MemberRegisterRequest registerRequest) {
        if (memberRepository.findByMemberId(new MemberId(registerRequest.memberId())).isPresent()) {
            throw new DuplicateMemberIdException("이미 사용중인 ID 입니다: " + registerRequest.memberId());
        }
    }

    @Transactional
    @Override
    public Member usePoint(MemberId memberId, BigDecimal discountedPrice) {
        Member member = memberFinder.findByMemberIdWithPessimisticLock(memberId);

        try {
            member.usePoint(discountedPrice);
        } catch (IllegalArgumentException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, e.getMessage());
        }
        return member;
    }
}
