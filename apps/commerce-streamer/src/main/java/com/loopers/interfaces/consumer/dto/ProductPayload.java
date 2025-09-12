package com.loopers.interfaces.consumer.dto;

import java.time.ZonedDateTime;

public record ProductPayload(Long productId, String eventId, ProductEventType eventType,
                             Long version, ZonedDateTime publishedAt, Long salePrice, Long cancelQuantity, Long saleQuantity) {

    public enum ProductEventType {
        PRODUCT_VIEW(0.1) {
            @Override
            public double calculateScore(ProductPayload payload) {
                return getWeight();
            }
        },
        PRODUCT_LIKE_INCREMENT(0.2) {
            @Override
            public double calculateScore(ProductPayload payload) {
                return getWeight();
            }
        },
        PRODUCT_LIKE_DECREMENT(0.2) {
            @Override
            public double calculateScore(ProductPayload payload) {
                return -1.0 * getWeight();
            }
        },
        PRODUCT_SALE(0.7) {
            @Override
            public double calculateScore(ProductPayload payload) {
                return payload.saleQuantity() * getWeight();
            }
        },
        PRODUCT_SALE_CANCEL(0.7) {
            @Override
            public double calculateScore(ProductPayload payload) {
                return -payload.cancelQuantity() * getWeight();
            }
        };

        private final double weight;

        ProductEventType(double weight) {
            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }

        public abstract double calculateScore(ProductPayload payload);
    }
}
