package com.loopers.application.inventory;

import com.loopers.application.inventory.provided.InventoryFinder;
import com.loopers.application.inventory.required.InventoryRepository;
import com.loopers.domain.inventory.Inventory;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;
import com.loopers.shared.stereotype.ApplicationService;

import lombok.RequiredArgsConstructor;

@ApplicationService
@RequiredArgsConstructor
public class InventoryQueryService implements InventoryFinder {
    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                                  .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "재고를 찾을 수 없습니다."));
    }
}
