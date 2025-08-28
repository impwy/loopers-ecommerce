package com.loopers.application.order;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.loopers.application.inventory.DecreaseInventoryRequest;
import com.loopers.application.product.ProductTotalAmountRequest;
import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.OrderRegister;
import com.loopers.application.provided.ProductFinder;
import com.loopers.domain.coupon.CouponUsed;
import com.loopers.domain.inventory.ProductInventoryUsed;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderWithCouponRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderRegister orderRegister;
    private final MemberFinder memberFinder;
    private final ProductFinder productFinder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderInfos order(MemberId memberId, CreateOrderWithCouponRequest createOrderWithCouponRequest) {
        Member member = memberFinder.findByMemberId(memberId);
        Long couponId = createOrderWithCouponRequest.couponId();

        List<CreateOrderRequest> orderRequests = createOrderWithCouponRequest.createOrderRequests();

        // 주문 생성
        List<CreateOrderItemSpec> createOrderItemSpecs
                = orderRequests.stream()
                               .map(request -> CreateOrderItemSpec.of(request.productId(), request.quantity()))
                               .toList();
        CreateOrderSpec createOrderSpec = CreateOrderSpec.of(member.getId());

        Order order = orderRegister.createOrder(createOrderSpec, createOrderItemSpecs);

        // 재고 차감
        List<DecreaseInventoryRequest> decreaseInventoryRequests
                = orderRequests.stream()
                               .map(request ->
                                            new DecreaseInventoryRequest(request.productId(), request.quantity()))
                               .toList();
        eventPublisher.publishEvent(new ProductInventoryUsed(decreaseInventoryRequests));

        // 쿠폰 생성 및 감소
        eventPublisher.publishEvent(new CouponUsed(couponId, member.getMemberId()));

        // 총 금액
        List<ProductTotalAmountRequest> productTotalAmountRequests
                = orderRequests.stream()
                               .map(request ->
                                            new ProductTotalAmountRequest(request.productId(), request.quantity()))
                               .toList();
        BigDecimal totalPrice = productFinder.getTotalPrice(productTotalAmountRequests);

        return OrderInfos.of(orderRequests, order, productFinder, totalPrice);
    }
}
