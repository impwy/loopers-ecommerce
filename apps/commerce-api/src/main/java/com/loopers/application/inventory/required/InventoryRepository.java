package com.loopers.application.inventory.required;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.inventory.Inventory;

import jakarta.persistence.LockModeType;

public interface InventoryRepository extends Repository<Inventory, Long> {
    Inventory save(Inventory inventory);

    Optional<Inventory> findById(Long inventoryId);

    Optional<Inventory> findByProductId(Long productId);

    List<Inventory> findAllByProductIdIn(List<Long> productIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.productId in :ids")
    List<Inventory> findAllByProductIdInWithPessimisticLock(@Param("ids") List<Long> ids);
}
