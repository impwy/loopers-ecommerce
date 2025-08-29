package com.loopers.application.inventory;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.provided.InventoryRegister;
import com.loopers.domain.inventory.InventoryRollback;
import com.loopers.domain.inventory.ProductInventoryUsed;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryEventHandler {
    private final InventoryRegister inventoryRegister;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ProductInventoryUsed event) {
        inventoryRegister.decreaseProducts(event.decreaseInventoryRequests());
    }

    @Async
    @EventListener
    public void handle(InventoryRollback inventoryRollback) {
        String orderId = inventoryRollback.orderId();

    }
}
