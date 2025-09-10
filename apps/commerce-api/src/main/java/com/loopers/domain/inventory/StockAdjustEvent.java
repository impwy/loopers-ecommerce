package com.loopers.domain.inventory;

public record StockAdjustEvent(Long outboxId, Long productId, Long quantity) {
}
