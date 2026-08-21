package com.loopers.adapter.webapi.order.dto;

import java.math.BigDecimal;

public class OrderV1Dto {
    public record OrderInfo(String orderNo, String productName, Long totalQuantity, BigDecimal totalPrice) {
        public static OrderInfo of(String orderNo, String productName, Long totalQuantity, BigDecimal totalPrice) {
            return new OrderInfo(orderNo, productName, totalQuantity, totalPrice);
        }

        public static OrderInfo from(com.loopers.application.order.OrderInfo orderInfo) {
            return new OrderInfo(orderInfo.orderNo(), orderInfo.productName(), orderInfo.totalQuantity(),
                                 orderInfo.totalPrice());
        }
    }
}
