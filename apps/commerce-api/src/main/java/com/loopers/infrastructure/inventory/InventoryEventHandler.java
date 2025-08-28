package com.loopers.infrastructure.inventory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.provided.InventoryRegister;
import com.loopers.domain.inventory.ProductInventoryUsed;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryEventHandler {
    private final InventoryRegister inventoryRegister;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductInventoryUsed event) {
        inventoryRegister.decreaseProducts(event.decreaseInventoryRequests());
    }
}
