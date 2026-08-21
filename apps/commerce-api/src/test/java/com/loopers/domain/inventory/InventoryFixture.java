package com.loopers.domain.inventory;

import com.loopers.application.inventory.CreateInventoryRequest;

import org.instancio.Instancio;

import static org.instancio.Select.field;

public final class InventoryFixture {
    private InventoryFixture() {
    }

    public static Inventory createInventory(Long productId, Long quantity) {
        return Instancio.of(Inventory.class)
                        .ignore(field(Inventory::getId))
                        .set(field(Inventory::getProductId), productId)
                        .set(field(Inventory::getQuantity), quantity)
                        .set(field(Inventory::getInventoryStatus), InventoryStatus.IN_SALE)
                        .create();
    }

    public static CreateInventorySpec createInventorySpec(Long productId, Long quantity) {
        return Instancio.of(CreateInventorySpec.class)
                        .set(field(CreateInventorySpec::productId), productId)
                        .set(field(CreateInventorySpec::quantity), quantity)
                        .create();
    }

    public static CreateInventoryRequest createInventoryRequest(Long productId, Long quantity) {
        return Instancio.of(CreateInventoryRequest.class)
                        .set(field(CreateInventoryRequest::productId), productId)
                        .set(field(CreateInventoryRequest::quantity), quantity)
                        .create();
    }
}
