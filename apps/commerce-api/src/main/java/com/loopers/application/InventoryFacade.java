package com.loopers.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.provided.ProductFinder;
import com.loopers.domain.inventory.CreateInventorySpec;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.product.Product;
import com.loopers.interfaces.api.inventory.dto.InventoryV1Dto.Request.CreateInventoryRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryFacade {
    private final ProductFinder productFinder;
    private final InventoryRegister inventoryRegister;

    @Transactional
    public Inventory createInventory(CreateInventoryRequest createInventoryRequest) {
        Product product = productFinder.find(createInventoryRequest.productId());

        CreateInventorySpec createInventorySpec = CreateInventorySpec.of(product.getId(),
                                                                         createInventoryRequest.quantity());
        return inventoryRegister.register(createInventorySpec);
    }

    public Inventory decrease(Long productId, Long quantity) {
        Product product = productFinder.find(productId);
        return inventoryRegister.decrease(product.getId(), quantity);
    }
}
