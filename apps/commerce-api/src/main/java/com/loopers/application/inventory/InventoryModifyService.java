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
import com.loopers.domain.inventory.DecreaseInventoryRequest;
import com.loopers.domain.inventory.Inventory;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryModifyService implements InventoryRegister {
    private final InventoryRepository inventoryRepository;
    private final InventoryFinder inventoryFinder;

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
                inventoryMap.get(orderRequest.productId()).decrease(orderRequest.quantity());
            });
        } catch (IllegalArgumentException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, e.getMessage());
        }

        return inventorys;
    }
}
