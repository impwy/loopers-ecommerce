package com.loopers.infrastructure.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.member.UserEventLog;

public interface UserEventLogJpaRepository extends JpaRepository<UserEventLog, Long> {
    Optional<UserEventLog> findTopByMemberIdAndEventTypeOrderByPublishedAtDesc(String memberId, String eventType);
}
