package com.loopers.application.inventory;

public record DecreaseInventoryRequest(Long productId, Long quantity) {
}
