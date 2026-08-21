package com.loopers.application.order;

import java.math.BigDecimal;

public record OrderInfo(String orderNo, String productName, Long totalQuantity, BigDecimal totalPrice) {
    public static OrderInfo of(String orderNo, String productName, Long totalQuantity, BigDecimal totalPrice) {
        return new OrderInfo(orderNo, productName, totalQuantity, totalPrice);
    }
}
