package com.loopers.application.provided;

import com.loopers.domain.Member;

public interface MemberFinder {
    Member find(Long memberId);
}
