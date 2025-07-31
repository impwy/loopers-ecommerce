package com.loopers.domain.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class InventoryTest {

    @DisplayName("재고량 0 이하 생성 시 실패")
    @ParameterizedTest
    @ValueSource(longs = { 0, -1 })
    void create_inventory_fail_test(Long quantity) {
        CoreException coreException = assertThrows(CoreException.class, () -> Inventory.create(1L, quantity));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
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

        CoreException coreException = assertThrows(CoreException.class, () -> inventory.decrease(11L));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("재고 품절일 때 감소 실패 테스트")
    @Test
    void decrease_inventory_fail_when_soldout_test() {
        Inventory inventory = Inventory.create(1L, 10L);
        inventory.decrease(10L);

        CoreException coreException = assertThrows(CoreException.class, () -> inventory.decrease(1L));

        assertThat(inventory.getInventoryStatus()).isEqualTo(InventoryStatus.SOLD_OUT);
        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("재고 감소 성공 테스트")
    @Test
    void decrease_inventory_test() {
        Inventory inventory = Inventory.create(1L, 10L);
        inventory.decrease(10L);

        assertThat(inventory.getQuantity()).isEqualTo(0L);
    }
}
