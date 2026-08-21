package com.loopers.application.member;

import java.math.BigDecimal;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.member.provided.MemberFinder;
import com.loopers.application.member.provided.MemberRegister;
import com.loopers.application.member.required.MemberRepository;
import com.loopers.domain.member.CreateMemberSpec;
import com.loopers.domain.member.DuplicateMemberIdException;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.PointUsageRequest;
import com.loopers.domain.member.UserId;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;
import com.loopers.shared.stereotype.ApplicationValidService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationValidService
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
    public Member chargePoint(UserId userId, BigDecimal amount) {
        Member member = memberFinder.findWithPoint(userId);
        member.charge(amount);
        member = memberRepository.save(member);

        return member;
    }

    private void checkDuplicateId(MemberRegisterRequest registerRequest) {
        if (memberRepository.findByUserId(new UserId(registerRequest.memberId())).isPresent()) {
            throw new DuplicateMemberIdException("이미 사용중인 ID 입니다: " + registerRequest.memberId());
        }
    }

    @Transactional
    @Override
    public Member usePoint(UserId userId, BigDecimal discountedPrice) {
        Member member = memberFinder.findByMemberIdWithPessimisticLock(userId);

        try {
            member.usePoint(discountedPrice);
        } catch (IllegalArgumentException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, e.getMessage());
        }
        return member;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PointUsageRequest event) {
        usePoint(event.userId(), event.totalAmount());
    }
}
