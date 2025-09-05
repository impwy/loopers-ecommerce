package com.loopers.domain.member;

import java.time.ZonedDateTime;

import com.loopers.interfaces.consumer.dto.UserPayload;

public record CreateUserEventLog(String memberId, String eventId, String eventType, Long version, ZonedDateTime publishedAt) {
    public static CreateUserEventLog of(UserPayload payload) {
        return new CreateUserEventLog(payload.memberId(), payload.eventId(), payload.eventType(),
                                      payload.version(), payload.publishedAt());
    }
}
