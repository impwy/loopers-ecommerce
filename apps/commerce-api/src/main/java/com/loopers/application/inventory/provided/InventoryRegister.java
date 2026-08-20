package com.loopers.application.inventory.provided;

import java.util.List;
import java.util.Map;

import com.loopers.application.inventory.DecreaseInventoryRequest;
import com.loopers.domain.inventory.CreateInventorySpec;
import com.loopers.domain.inventory.Inventory;

import jakarta.validation.Valid;

public interface InventoryRegister {
    Inventory register(@Valid CreateInventorySpec createInventorySpec);

    Inventory decrease(Long productId, Long quantity);

    List<Inventory> decreaseProducts(List<DecreaseInventoryRequest> decreaseInventoryRequests);

    void rollbackInventory(List<Long> productIds, Map<Long, Long> productQuantityMap);
}
