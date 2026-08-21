package com.loopers.domain.inventory;

import java.util.List;

public record ProductInventoryUsed(List<DecreaseInventoryRequest> decreaseInventoryRequests) {
}

