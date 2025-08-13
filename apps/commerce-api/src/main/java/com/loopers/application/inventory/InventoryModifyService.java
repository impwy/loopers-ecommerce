package com.loopers.application.inventory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.InventoryFinder;
import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.required.InventoryRepository;
import com.loopers.domain.inventory.CreateInventorySpec;
import com.loopers.domain.inventory.Inventory;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryModifyService implements InventoryRegister {
    private final InventoryRepository inventoryRepository;
    private final InventoryFinder inventoryFinder;

    @Override
    public Inventory register(CreateInventorySpec createInventorySpec) {
        Inventory inventory = Inventory.of(createInventorySpec);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory decrease(Long productId, Long quantity) {
        Inventory inventory = inventoryFinder.findByProductId(productId);

        inventory.decrease(quantity);

        return inventory;
    }

    @Transactional
    @Override
    public List<Inventory> decreaseProducts(List<CreateOrderRequest> orderRequests) {
        List<Long> productIds = orderRequests.stream().map(CreateOrderRequest::productId).toList();
        List<Inventory> inventorys = inventoryRepository.findByProductIdWithPessimisticLock(productIds);
        Map<Long, Inventory> inventoryMap = inventorys.stream().collect(Collectors.toMap(Inventory::getId, Function.identity()));

        orderRequests.forEach(orderRequest -> {
            inventoryMap.get(orderRequest.productId()).decrease(orderRequest.quantity());
        });

        return inventorys;
    }
}
