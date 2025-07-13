package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.Member;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findByMemberId(String memberId);
}
