package com.loopers.application.inventory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.InventoryFinder;
import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.provided.ProductOutboxRegister;
import com.loopers.application.required.InventoryRepository;
import com.loopers.domain.inventory.CreateInventorySpec;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.inventory.StockAdjustEvent;
import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryModifyService implements InventoryRegister {
    private final InventoryRepository inventoryRepository;
    private final InventoryFinder inventoryFinder;
    private final ApplicationEventPublisher eventPublisher;
    private final ProductOutboxRegister productOutboxRegister;

    @Override
    public Inventory register(CreateInventorySpec createInventorySpec) {
        try {
            Inventory inventory = Inventory.of(createInventorySpec);
            return inventoryRepository.save(inventory);
        } catch (IllegalArgumentException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public Inventory decrease(Long productId, Long quantity) {
        Inventory inventory = inventoryFinder.findByProductId(productId);

        try {
            inventory.decrease(quantity);
        } catch (IllegalArgumentException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, e.getMessage());
        }

        return inventory;
    }

    @Transactional
    @Override
    public List<Inventory> decreaseProducts(List<DecreaseInventoryRequest> decreaseInventoryRequests) {
        List<Long> productIds = decreaseInventoryRequests.stream().map(DecreaseInventoryRequest::productId).toList();
        List<Inventory> inventorys = inventoryRepository.findByProductIdWithPessimisticLock(productIds);
        Map<Long, Inventory> inventoryMap = inventorys.stream().collect(Collectors.toMap(Inventory::getId, Function.identity()));

        try {
            decreaseInventoryRequests.forEach(orderRequest -> {
                Long productId = orderRequest.productId();
                Long saleQuantity = orderRequest.quantity();

                inventoryMap.get(productId).decrease(saleQuantity);

                String uuid = UUID.randomUUID().toString();
                CreateProductOutbox createProductOutbox = new CreateProductOutbox(productId, uuid, ProductEventType.PRODUCT_SALE,
                                                                                  0L, ZonedDateTime.now());
                ProductEventOutbox productEventOutbox = productOutboxRegister.register(createProductOutbox);
                Long outboxId = productEventOutbox.getId();
                eventPublisher.publishEvent(new StockAdjustEvent(outboxId, productId, saleQuantity));
            });
        } catch (IllegalArgumentException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, e.getMessage());
        }

        return inventorys;
    }

    @Override
    public void rollbackInventory(List<Long> productIds, Map<Long, Long> productQuantityMap) {
        List<Inventory> inventories = inventoryRepository.findAllByProductIdIn(productIds);

        inventories.forEach(inventory -> {
            Long productId = inventory.getProductId();
            Long cancelQuantity = productQuantityMap.get(productId);

            inventory.increase(cancelQuantity);
        });
    }
}
