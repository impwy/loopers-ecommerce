package com.loopers.application.provided;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;

public interface MemberFinder {
    Member find(Long memberId);

    Member findByMemberId(MemberId memberId);
}
