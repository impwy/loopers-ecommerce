package com.loopers.application.provided;

import com.loopers.domain.inventory.Inventory;

public interface InventoryFinder {
    Inventory findByProductId(Long productId);
}
