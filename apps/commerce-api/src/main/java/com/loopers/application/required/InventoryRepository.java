package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.inventory.Inventory;

public interface InventoryRepository {
    Inventory save(Inventory inventory);

    Optional<Inventory> find(Long inventoryId);

    Optional<Inventory> findByProductId(Long productId);
}
