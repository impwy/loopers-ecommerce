package com.loopers.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(MemberId memberId);
}
