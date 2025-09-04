package com.loopers.application.inventory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.provided.OrderFinder;
import com.loopers.application.required.InventoryRepository;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.inventory.InventoryRollback;
import com.loopers.domain.inventory.ProductInventoryUsed;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.orderitem.OrderItem;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryEventHandler {
    private final InventoryRegister inventoryRegister;
    private final OrderFinder orderFinder;
    private final InventoryRepository inventoryRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ProductInventoryUsed event) {
        inventoryRegister.decreaseProducts(event.decreaseInventoryRequests());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(InventoryRollback inventoryRollback) {
        String orderId = inventoryRollback.orderId();
        Order order = orderFinder.findByOrderNo(orderId);

        Map<Long, Long> productQuantityMap = order.getOrderItems().stream()
                                                  .collect(Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity));

        List<Long> productIds = order.getOrderItems().stream().map(OrderItem::getProductId).toList();

        List<Inventory> inventories = inventoryRepository.findAllByProductIdIn(productIds);

        inventories.forEach(inventory -> {
            Long quantity = productQuantityMap.get(inventory.getProductId());
            inventory.increase(quantity);
        });
    }
}
