package com.loopers.interfaces.api.order.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderV1Dto {
    public class Request {
        public record CreateOrderWithCouponRequest(List<CreateOrderRequest> createOrderRequests, Long couponId) {
            public static CreateOrderWithCouponRequest create(List<CreateOrderRequest> createOrderRequests, Long couponId) {
                return new CreateOrderWithCouponRequest(createOrderRequests, couponId);
            }
        }

        public record CreateOrderRequest(Long productId, Long quantity) {
            public static CreateOrderRequest of(Long productId, Long quantity) {
                return new CreateOrderRequest(productId, quantity);
            }
        }
    }

    public class Response {
        public record OrderInfo(String orderNo, String productName, Long totalQuantity, BigDecimal totalPrice) {
            public static OrderInfo of(String orderNo, String productName, Long totalQuantity, BigDecimal totalPrice) {
                return new OrderInfo(orderNo, productName, totalQuantity, totalPrice);
            }
        }
    }
}
