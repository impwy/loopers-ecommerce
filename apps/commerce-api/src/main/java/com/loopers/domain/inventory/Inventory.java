package com.loopers.domain.inventory;

import static java.util.Objects.requireNonNull;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "inventory")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory extends BaseEntity {
    private Long productId;

    private Long quantity;

    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus;

    private Inventory(Long productId, Long quantity) {
        this.productId = requireNonNull(productId);
        this.quantity = requireNonNull(quantity);
        this.inventoryStatus = InventoryStatus.IN_SALE;
    }

    public static Inventory create(Long productId, Long quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("재고량은 0 미만이 될 수 없습니다: " + quantity);
        }
        return new Inventory(productId, quantity);
    }

    public static Inventory of(CreateInventorySpec createInventorySpec) {
        return create(createInventorySpec.productId(), createInventorySpec.quantity());
    }

    public void decrease(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("잘못 된 요청 값입니다. : " + quantity);
        }

        if (this.quantity <= 0 || this.quantity < quantity) {
            throw new IllegalArgumentException("잔여 재고가 없습니다.");
        }

        if (inventoryStatus == InventoryStatus.SOLD_OUT) {
            throw new IllegalArgumentException("품절입니다.");
        }

        this.quantity -= quantity;

        if (this.quantity == 0) {
            this.inventoryStatus = InventoryStatus.SOLD_OUT;
        }
    }

    public void increase(Long quantity) {
        this.quantity += quantity;
    }
}
