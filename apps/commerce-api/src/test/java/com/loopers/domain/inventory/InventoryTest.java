package com.loopers.domain.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InventoryTest {

    @DisplayName("재고량 0 이하 생성 시 실패")
    @ParameterizedTest
    @ValueSource(longs = { -1, -2, -3 })
    void create_inventory_fail_test(Long quantity) {
        assertThatThrownBy(() -> Inventory.create(1L, quantity))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 ID가 NULL일 때")
    @Test
    void create_inventory_fail_productId_is_null_test() {
        assertThatThrownBy(() -> Inventory.create(null, 1L))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("충전하려는 재고가 NULL일 때")
    @Test
    void create_inventory_fail_quantity_is_null_test() {
        assertThatThrownBy(() -> Inventory.create(1L, null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("재고 생성 성공 테스트")
    @Test
    void create_inventory_test() {
        Inventory inventory = Inventory.create(1L, 10L);

        assertThat(inventory.getProductId()).isEqualTo(1L);
        assertThat(inventory.getQuantity()).isEqualTo(10L);
    }

    @DisplayName("재고 감소 실패 테스트")
    @Test
    void decrease_inventory_fail_test() {
        Inventory inventory = Inventory.create(1L, 10L);

        assertThatThrownBy(() -> inventory.decrease(11L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("재고 품절일 때 감소 실패 테스트")
    @Test
    void decrease_inventory_fail_when_soldout_test() {
        Inventory inventory = Inventory.create(1L, 10L);
        inventory.decrease(10L);

        assertThatThrownBy(() -> inventory.decrease(1L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(inventory.getInventoryStatus()).isEqualTo(InventoryStatus.SOLD_OUT);
    }

    @DisplayName("재고 감소 성공 테스트")
    @Test
    void decrease_inventory_test() {
        Inventory inventory = Inventory.create(1L, 10L);
        inventory.decrease(10L);

        assertThat(inventory.getQuantity()).isEqualTo(0L);
    }
}
