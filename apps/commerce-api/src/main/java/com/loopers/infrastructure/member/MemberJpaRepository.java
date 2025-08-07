package com.loopers.infrastructure.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;

import jakarta.persistence.LockModeType;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(MemberId memberId);

    @Query("SELECT m FROM Member m JOIN FETCH m.point WHERE m.memberId = :memberId")
    Optional<Member> findWithPoint(@Param("memberId") MemberId memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.memberId = :memberId")
    Optional<Member> findByMemberIdWithPessimisticLock(@Param("memberId") MemberId memberId);
}
