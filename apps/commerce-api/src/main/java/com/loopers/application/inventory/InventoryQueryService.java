package com.loopers.application.inventory;

import org.springframework.stereotype.Service;

import com.loopers.application.inventory.provided.InventoryFinder;
import com.loopers.application.inventory.required.InventoryRepository;
import com.loopers.domain.inventory.Inventory;
import com.loopers.share.error.CoreException;
import com.loopers.share.error.ErrorType;

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
