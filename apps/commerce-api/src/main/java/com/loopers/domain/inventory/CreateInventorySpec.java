package com.loopers.domain.inventory;

public record CreateInventorySpec(Long productId, Long quantity) {
    public static CreateInventorySpec of(Long productId, Long quantity) {
        return new CreateInventorySpec(productId, quantity);
    }
}
