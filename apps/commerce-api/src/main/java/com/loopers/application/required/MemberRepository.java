package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> find(Long memberId);

    Optional<Member> findByMemberId(MemberId memberId);

    Optional<Member> findWithPoint(MemberId memberId);

    Optional<Member> findByMemberIdWithPessimisticLock(MemberId memberId);
}
