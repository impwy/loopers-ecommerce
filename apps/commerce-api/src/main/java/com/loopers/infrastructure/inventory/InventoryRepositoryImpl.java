package com.loopers.infrastructure.inventory;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.loopers.application.required.InventoryRepository;
import com.loopers.domain.inventory.Inventory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {
    private final InventoryJpaRepository inventoryJpaRepository;

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryJpaRepository.save(inventory);
    }

    @Override
    public Optional<Inventory> find(Long inventoryId) {
        return inventoryJpaRepository.findById(inventoryId);
    }

    @Override
    public Optional<Inventory> findByProductId(Long productId) {
        return inventoryJpaRepository.findByProductId(productId);
    }

    @Override
    public List<Inventory> findByProductIdWithPessimisticLock(List<Long> productIds) {
        return inventoryJpaRepository.findAllByProductIdInWithPessimisticLock(productIds);
    }

    @Override
    public List<Inventory> findAllByProductIdIn(List<Long> productIds) {
        return inventoryJpaRepository.findAllByProductIdIn(productIds);
    }
}
