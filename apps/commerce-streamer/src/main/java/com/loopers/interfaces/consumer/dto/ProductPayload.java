package com.loopers.interfaces.consumer.dto;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class ProductPayload {
    private Long productId;
    private String eventId;
    private String eventType;
    private Long version;
    private ZonedDateTime publishedAt;
}
