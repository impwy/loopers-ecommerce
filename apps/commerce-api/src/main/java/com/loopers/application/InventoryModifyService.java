package com.loopers.application;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.required.InventoryRepository;
import com.loopers.domain.inventory.CreateInventorySpec;
import com.loopers.domain.inventory.Inventory;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryModifyService implements InventoryRegister {
    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory register(CreateInventorySpec createInventorySpec) {
        Inventory inventory = Inventory.of(createInventorySpec);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory decrease(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                                     .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "재고를 찾을 수 없습니다."));

        inventory.decrease(quantity);

        return inventory;
    }
}
