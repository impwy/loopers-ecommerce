package com.loopers.interfaces.consumer.dto;

import java.time.ZonedDateTime;

public record ProductPayload(Long productId, String eventId, ProductEventType eventType,
                             Long version, ZonedDateTime publishedAt, Long salePrice, Long cancelQuantity,
                             Long likeCount, Long viewCount, Long saleQuantity) {

    public enum ProductEventType {
        PRODUCT_LIKE_INCREMENT,
        PRODUCT_LIKE_DECREMENT,
        PRODUCT_SALE,
        PRODUCT_SALE_CANCEL,
        PRODUCT_VIEW
    }
}
