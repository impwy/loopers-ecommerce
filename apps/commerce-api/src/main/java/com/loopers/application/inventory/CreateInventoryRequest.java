package com.loopers.application.inventory;

public record CreateInventoryRequest(Long productId, Long quantity) {
    public static CreateInventoryRequest of(Long productId, Long quantity) {
        return new CreateInventoryRequest(productId, quantity);
    }
}
