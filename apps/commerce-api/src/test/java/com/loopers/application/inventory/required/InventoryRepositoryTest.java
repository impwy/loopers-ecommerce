package com.loopers.application.inventory.required;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.loopers.domain.inventory.InventoryFixture;
import com.loopers.domain.inventory.Inventory;
import com.loopers.support.BaseRepositoryTest;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class InventoryRepositoryTest extends BaseRepositoryTest {
    final InventoryRepository inventoryRepository;

    @Test
    void saveAndFindById() {
        Inventory inventory = inventoryRepository.save(InventoryFixture.createInventory(1L, 10L));
        flushAndClear();

        Inventory found = inventoryRepository.findById(inventory.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(inventory.getId());
        assertThat(found.getProductId()).isEqualTo(1L);
        assertThat(found.getQuantity()).isEqualTo(10L);
    }

    @Test
    void findByProductId() {
        Inventory inventory = inventoryRepository.save(InventoryFixture.createInventory(2L, 20L));
        flushAndClear();

        Inventory found = inventoryRepository.findByProductId(2L).orElseThrow();

        assertThat(found.getId()).isEqualTo(inventory.getId());
    }

    @Test
    void findAllByProductIdIn() {
        inventoryRepository.save(InventoryFixture.createInventory(3L, 30L));
        inventoryRepository.save(InventoryFixture.createInventory(4L, 40L));
        flushAndClear();

        List<Inventory> found = inventoryRepository.findAllByProductIdIn(List.of(3L, 4L));

        assertThat(found).extracting(Inventory::getProductId).containsExactlyInAnyOrder(3L, 4L);
    }

    @Test
    void findAllByProductIdInWithPessimisticLock() {
        inventoryRepository.save(InventoryFixture.createInventory(5L, 50L));
        flushAndClear();

        List<Inventory> found = inventoryRepository.findAllByProductIdInWithPessimisticLock(List.of(5L));

        assertThat(found).singleElement().extracting(Inventory::getProductId).isEqualTo(5L);
    }
}
