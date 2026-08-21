package com.loopers.application.inventory.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.inventory.CreateInventorySpec;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.inventory.InventoryStatus;
import com.loopers.domain.product.Product;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationValidServiceTest;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

@ApplicationValidServiceTest
@RequiredArgsConstructor
public class InventoryRegisterTest extends BaseApplicationServiceTest {
    final InventoryRegister inventoryRegister;

    Product product;
    Brand brand;

    @BeforeEach
    void setUp() {
        brand = prepareBrand();
        product = prepareProduct(brand);
    }

    @DisplayName("음수로 재고 생성 요청 시 실패")
    @Test
    void create_inventory_fail_when_quantity_is_negative() {
        CreateInventorySpec createInventorySpec = CreateInventorySpec.of(product.getId(), -1L);

        assertThatThrownBy(() -> inventoryRegister.register(createInventorySpec))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @DisplayName("재고 생성 성공 테스트")
    @Test
    void create_inventory_test() {
        CreateInventorySpec createInventorySpec = CreateInventorySpec.of(product.getId(), 1000L);
        Inventory expected = inventoryRegister.register(createInventorySpec);

        assertThat(expected.getProductId()).isEqualTo(product.getId());
        assertThat(expected.getQuantity()).isEqualTo(1000L);
        assertThat(expected.getInventoryStatus()).isEqualTo(InventoryStatus.IN_SALE);
    }

    @DisplayName("재고가 0일 때 재고 감소 실패 테스트")
    @Test
    void create_inventory_fail_when_inventory_zero_test() {
        prepareInventory(product, 0L);

        CoreException coreException = assertThrows(CoreException.class, () -> inventoryRegister.decrease(product.getId(), 1L));
        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("잘못 된 재고 감소 요청 값 실패 테스트")
    @ParameterizedTest
    @ValueSource(longs = { 0, -1, -2 })
    void decrease_inventory_fail_when_quantity_is_wrong_test(long quantity) {
        Long productId = product.getId();
        prepareInventory(productId, 100L);

        CoreException coreException = assertThrows(CoreException.class, () -> inventoryRegister.decrease(productId, quantity));
        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("재고 감소 요청 성공")
    @Test
    void decrease_inventory_test() {
        Long productId = product.getId();
        prepareInventory(productId, 100L);

        Inventory expected = inventoryRegister.decrease(productId, 1L);

        assertThat(expected.getProductId()).isEqualTo(product.getId());
        assertThat(expected.getQuantity()).isEqualTo(99L);
        assertThat(expected.getInventoryStatus()).isEqualTo(InventoryStatus.IN_SALE);
    }

    @DisplayName("재고 감소 요청 성공 후 재고 0일 시 품절 상태 변경")
    @Test
    void decrease_inventory_status_soldout_test() {
        Long productId = product.getId();
        prepareInventory(productId, 10L);

        Inventory expected = inventoryRegister.decrease(productId, 10L);

        assertThat(expected.getProductId()).isEqualTo(product.getId());
        assertThat(expected.getQuantity()).isEqualTo(0L);
        assertThat(expected.getInventoryStatus()).isEqualTo(InventoryStatus.SOLD_OUT);
    }
}
