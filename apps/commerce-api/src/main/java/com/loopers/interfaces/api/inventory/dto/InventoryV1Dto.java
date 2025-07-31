package com.loopers.interfaces.api.inventory.dto;

public class InventoryV1Dto {
    public class Request {
        public record CreateInventoryRequest(Long productId, Long quantity) {
            public static CreateInventoryRequest of(Long productId, Long quantity) {
                return new CreateInventoryRequest(productId, quantity);
            }
        }
    }
}
