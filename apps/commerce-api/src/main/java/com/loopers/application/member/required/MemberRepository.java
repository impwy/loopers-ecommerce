package com.loopers.application.member.required;

import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.UserId;

import jakarta.persistence.LockModeType;

public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);

    Optional<Member> findById(Long memberId);

    Optional<Member> findByUserId(UserId userId);

    @Query("SELECT m FROM Member m JOIN FETCH m.point WHERE m.userId = :userId")
    Optional<Member> findWithPoint(@Param("userId") UserId userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.userId = :userId")
    Optional<Member> findByUserIdWithPessimisticLock(@Param("userId") UserId userId);
}
