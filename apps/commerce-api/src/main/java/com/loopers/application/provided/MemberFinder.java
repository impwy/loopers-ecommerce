package com.loopers.application.provided;

import java.util.Optional;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;

public interface MemberFinder {
    Member find(Long memberId);

    Member findByMemberId(MemberId memberId);

    Member findByMemberIdWithPessimisticLock(MemberId memberId);
}
