package com.loopers.application.inventory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.provided.InventoryFinder;
import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.provided.OrderFinder;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.inventory.InventoryRollback;
import com.loopers.domain.inventory.ProductInventoryUsed;
import com.loopers.domain.order.Order;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryEventHandler {
    private final InventoryRegister inventoryRegister;
    private final OrderFinder orderFinder;
    private final InventoryFinder inventoryFinder;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ProductInventoryUsed event) {
        inventoryRegister.decreaseProducts(event.decreaseInventoryRequests());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(InventoryRollback inventoryRollback) {
        String orderId = inventoryRollback.orderId();
        Order order = orderFinder.findByOrderNo(orderId);
        order.getOrderItems().forEach(orderItem -> {
            Inventory inventory = inventoryFinder.findByProductId(orderItem.getProductId());
            inventory.increase(orderItem.getQuantity());
        });
    }
}
