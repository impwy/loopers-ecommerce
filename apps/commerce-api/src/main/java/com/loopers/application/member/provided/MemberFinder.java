package com.loopers.application.member.provided;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.UserId;

public interface MemberFinder {
    Member find(Long memberId);

    Member findByUserId(UserId userId);

    Member findWithPoint(UserId userId);

    Member findByMemberIdWithPessimisticLock(UserId userId);
}
