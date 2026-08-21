package com.loopers.domain.order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.application.order.CreateOrderRequest;
import com.loopers.application.order.CreateOrderWithCouponRequest;

import org.instancio.Instancio;

import static org.instancio.Select.field;

public record OrderFixture() {
    private static final AtomicLong ORDER_NO_SEQUENCE = new AtomicLong(1);

    public static Order createOrder() {
        return createOrder(1L);
    }

    public static Order createOrder(Long memberId) {
        return Instancio.of(Order.class)
                        .ignore(field(Order::getId))
                        .set(field(Order::getMemberId), memberId)
                        .set(field(Order::getOrderNo), nextOrderNo())
                        .set(field(Order::getOrderStatus), OrderStatus.PENDING)
                        .set(field(Order::getOrderItems), new ArrayList<>())
                        .create();
    }

    private static OrderNo nextOrderNo() {
        return new OrderNo("20250713" + String.format("%06d", ORDER_NO_SEQUENCE.getAndIncrement()));
    }

    public static CreateOrderSpec createOrderSpec(Long memberId) {
        return Instancio.of(CreateOrderSpec.class)
                        .set(field(CreateOrderSpec::memberId), memberId)
                        .create();
    }

    public static CreateOrderItemSpec createOrderItemSpec(Long productId, Long quantity, Long couponId) {
        return Instancio.of(CreateOrderItemSpec.class)
                        .set(field(CreateOrderItemSpec::productId), productId)
                        .set(field(CreateOrderItemSpec::quantity), quantity)
                        .set(field(CreateOrderItemSpec::couponId), couponId)
                        .create();
    }

    public static CreateOrderRequest createOrderRequest(Long productId, Long quantity) {
        return Instancio.of(CreateOrderRequest.class)
                        .set(field(CreateOrderRequest::productId), productId)
                        .set(field(CreateOrderRequest::quantity), quantity)
                        .create();
    }

    public static CreateOrderWithCouponRequest createOrderWithCouponRequest(List<CreateOrderRequest> requests,
                                                                             Long couponId) {
        return Instancio.of(CreateOrderWithCouponRequest.class)
                        .set(field(CreateOrderWithCouponRequest::createOrderRequests), requests)
                        .set(field(CreateOrderWithCouponRequest::couponId), couponId)
                        .create();
    }
}
