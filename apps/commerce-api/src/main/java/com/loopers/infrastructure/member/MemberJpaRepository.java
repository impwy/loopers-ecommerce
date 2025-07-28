package com.loopers.infrastructure.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(MemberId memberId);

    @Query("SELECT m FROM Member m JOIN FETCH m.point WHERE m.id = :memberId")
    Optional<Member> findWithPoint(@Param("memberId") Long memberId);
}
