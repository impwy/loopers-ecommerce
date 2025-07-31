package com.loopers.interfaces.api.order.dto;

import java.math.BigDecimal;

public class OrderV1Dto {
    public class Request {
        public record CreateOrderRequest(Long productId, Long quantity) {
            public static CreateOrderRequest of(Long productId, Long quantity) {
                return new CreateOrderRequest(productId, quantity);
            }
        }
    }

    public class Response {
        public record OrderProductInfo(String orderNo, String productName, Long totalQuantity, BigDecimal totalPrice) {
            public static OrderProductInfo of(String orderNo, String productName, Long totalQuantity, BigDecimal totalPrice) {
                return new OrderProductInfo(orderNo, productName, totalQuantity, totalPrice);
            }
        }
    }
}
