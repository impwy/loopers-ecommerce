package com.loopers.application.required;

import java.util.List;
import java.util.Optional;

import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.product.Product;

public interface InventoryRepository {
    Inventory save(Inventory inventory);

    Optional<Inventory> find(Long inventoryId);

    Optional<Inventory> findByProductId(Long productId);

    List<Inventory> findByProductIdWithPessimisticLock(List<Long> productIds);
}
