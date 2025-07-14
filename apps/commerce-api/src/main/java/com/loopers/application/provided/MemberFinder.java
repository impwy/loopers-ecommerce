package com.loopers.application.provided;

import com.loopers.domain.member.Member;

public interface MemberFinder {
    Member find(Long memberId);
}
