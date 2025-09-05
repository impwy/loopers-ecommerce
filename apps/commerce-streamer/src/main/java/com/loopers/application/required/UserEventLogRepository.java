package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.member.UserEventLog;

public interface UserEventLogRepository {
    Optional<UserEventLog> findTopByMemberIdAndEventTypeOrderByPublishedAtDesc(String memberId, String eventType);

    UserEventLog save(UserEventLog newLog);
}
