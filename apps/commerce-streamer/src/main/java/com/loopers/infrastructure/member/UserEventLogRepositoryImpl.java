package com.loopers.infrastructure.member;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.loopers.application.required.UserEventLogRepository;
import com.loopers.domain.member.UserEventLog;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEventLogRepositoryImpl implements UserEventLogRepository {
    private final UserEventLogJpaRepository userEventLogJpaRepository;

    @Override
    public Optional<UserEventLog> findTopByMemberIdAndEventTypeOrderByPublishedAtDesc(String memberId, String eventType) {
        return userEventLogJpaRepository.findTopByMemberIdAndEventTypeOrderByPublishedAtDesc(memberId, eventType);
    }

    @Override
    public UserEventLog save(UserEventLog newLog) {
        return userEventLogJpaRepository.save(newLog);
    }
}
