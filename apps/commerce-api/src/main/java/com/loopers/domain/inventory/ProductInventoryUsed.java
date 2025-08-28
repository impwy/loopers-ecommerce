package com.loopers.domain.inventory;

import java.util.List;

import com.loopers.application.inventory.DecreaseInventoryRequest;

public record ProductInventoryUsed(List<DecreaseInventoryRequest> decreaseInventoryRequests) {
}

