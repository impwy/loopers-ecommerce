package com.loopers.application.provided;

import com.loopers.domain.member.UserEventLog;

public interface UserEventLogFinder {
    UserEventLog findTopByMemberIdAndEventTypeOrderByPublishedAtDesc(String memberId, String eventType);
}
