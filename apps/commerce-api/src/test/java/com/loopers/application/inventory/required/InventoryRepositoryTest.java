package com.loopers.application.inventory.required;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import com.loopers.domain.inventory.Inventory;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class InventoryRepositoryTest {
    final InventoryRepository inventoryRepository;
    final EntityManager entityManager;

    @Test
    void saveAndFindById() {
        Inventory inventory = inventoryRepository.save(Inventory.create(1L, 10L));
        entityManager.flush();
        entityManager.clear();

        Inventory found = inventoryRepository.findById(inventory.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(inventory.getId());
        assertThat(found.getProductId()).isEqualTo(1L);
        assertThat(found.getQuantity()).isEqualTo(10L);
    }

    @Test
    void findByProductId() {
        Inventory inventory = inventoryRepository.save(Inventory.create(2L, 20L));
        entityManager.flush();
        entityManager.clear();

        Inventory found = inventoryRepository.findByProductId(2L).orElseThrow();

        assertThat(found.getId()).isEqualTo(inventory.getId());
    }

    @Test
    void findAllByProductIdIn() {
        inventoryRepository.save(Inventory.create(3L, 30L));
        inventoryRepository.save(Inventory.create(4L, 40L));
        entityManager.flush();
        entityManager.clear();

        List<Inventory> found = inventoryRepository.findAllByProductIdIn(List.of(3L, 4L));

        assertThat(found).extracting(Inventory::getProductId).containsExactlyInAnyOrder(3L, 4L);
    }

    @Test
    void findAllByProductIdInWithPessimisticLock() {
        inventoryRepository.save(Inventory.create(5L, 50L));
        entityManager.flush();
        entityManager.clear();

        List<Inventory> found = inventoryRepository.findAllByProductIdInWithPessimisticLock(List.of(5L));

        assertThat(found).singleElement().extracting(Inventory::getProductId).isEqualTo(5L);
    }
}
