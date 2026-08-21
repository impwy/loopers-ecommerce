package com.loopers.domain.inventory;

public record DecreaseInventoryRequest(Long productId, Long quantity) {
}
