package com.loopers.infrastructure.inventory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.inventory.Inventory;

import jakarta.persistence.LockModeType;

public interface InventoryJpaRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    List<Inventory> findAllByProductIdIn(List<Long> productIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i where i.productId in :ids")
    List<Inventory> findAllByProductIdInWithPessimisticLock(@Param("ids") List<Long> ids);
}
