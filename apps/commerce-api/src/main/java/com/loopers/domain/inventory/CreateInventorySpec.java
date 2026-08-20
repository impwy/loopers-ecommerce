package com.loopers.domain.inventory;

import jakarta.validation.constraints.Min;

public record CreateInventorySpec(Long productId, @Min(0) Long quantity) {
    public static CreateInventorySpec of(Long productId, Long quantity) {
        return new CreateInventorySpec(productId, quantity);
    }
}
