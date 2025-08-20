package com.loopers.application.inventory;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.InventoryFinder;
import com.loopers.application.required.InventoryRepository;
import com.loopers.domain.inventory.Inventory;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryQueryService implements InventoryFinder {
    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                                  .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "재고를 찾을 수 없습니다."));
    }
}
