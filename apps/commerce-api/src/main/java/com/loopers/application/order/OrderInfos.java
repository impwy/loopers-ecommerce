package com.loopers.application.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.loopers.application.provided.ProductFinder;
import com.loopers.domain.order.Order;
import com.loopers.domain.product.Product;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Response.OrderInfo;

import lombok.Getter;

@Getter
public class OrderInfos {
    private final List<OrderInfo> orderInfos;

    public OrderInfos(List<OrderInfo> orderInfos) {
        if (orderInfos == null || orderInfos.isEmpty()) {
            throw new IllegalArgumentException("OrderInfo 목록이 비어있을 수 없습니다.");
        }
        this.orderInfos = List.copyOf(orderInfos);
    }

    public static OrderInfos of(List<CreateOrderRequest> orderRequests, Order order, ProductFinder productFinder) {
        List<Long> productIds = orderRequests.stream()
                                             .map(CreateOrderRequest::productId)
                                             .distinct()
                                             .toList();
        Map<Long, Product> productMap = productFinder.getProductMap(productIds);

        List<OrderInfo> orderInfos = orderRequests.stream().map(c -> {
            Product product = productMap.get(c.productId());
            return OrderInfo.of(order.getOrderNo().value(),
                                product.getName(),
                                c.quantity(),
                                product.getTotalPrice(BigDecimal.valueOf(c.quantity())));
        }).toList();
        return new OrderInfos(orderInfos);
    }
}
