package com.loopers.application.provided;

import java.util.List;

import com.loopers.domain.inventory.CreateInventorySpec;
import com.loopers.domain.inventory.DecreaseInventoryRequest;
import com.loopers.domain.inventory.Inventory;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;

public interface InventoryRegister {
    Inventory register(CreateInventorySpec createInventorySpec);

    Inventory decrease(Long productId, Long quantity);

    List<Inventory> decreaseProducts(List<DecreaseInventoryRequest> decreaseInventoryRequests);
}
