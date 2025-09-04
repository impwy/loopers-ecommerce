package com.loopers.interfaces.consumer.dto;

import java.time.ZonedDateTime;

public record UserPayload(String memberId, String eventId, String eventType, Long version, ZonedDateTime publishedAt) {
}
