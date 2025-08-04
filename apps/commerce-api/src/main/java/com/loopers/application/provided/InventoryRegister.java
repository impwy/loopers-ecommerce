package com.loopers.application.provided;

import com.loopers.domain.inventory.CreateInventorySpec;
import com.loopers.domain.inventory.Inventory;

public interface InventoryRegister {
    Inventory register(CreateInventorySpec createInventorySpec);

    Inventory decrease(Long productId, Long quantity);
}
