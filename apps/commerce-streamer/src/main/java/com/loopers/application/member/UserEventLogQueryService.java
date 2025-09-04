package com.loopers.application.member;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.UserEventLogFinder;
import com.loopers.application.required.UserEventLogRepository;
import com.loopers.domain.member.UserEventLog;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEventLogQueryService implements UserEventLogFinder {
    private final UserEventLogRepository userEventLogRepository;

    @Override
    public UserEventLog findTopByMemberIdAndEventTypeOrderByPublishedAtDesc(String memberId, String eventType) {
        return userEventLogRepository.findTopByMemberIdAndEventTypeOrderByPublishedAtDesc(memberId, eventType)
                                     .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다."));
    }
}
