package com.loopers.application.order;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.CouponRegister;
import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.MemberRegister;
import com.loopers.application.provided.OrderRegister;
import com.loopers.application.provided.ProductFinder;
import com.loopers.domain.inventory.DecreaseInventoryRequest;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.domain.product.ProductTotalAmountRequest;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderWithCouponRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderRegister orderRegister;
    private final MemberFinder memberFinder;
    private final MemberRegister memberRegister;
    private final InventoryRegister inventoryRegister;
    private final CouponRegister couponRegister;
    private final ProductFinder productFinder;

    public OrderInfos register(MemberId memberId, CreateOrderWithCouponRequest createOrderWithCouponRequest) {
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
        inventoryRegister.decreaseProducts(decreaseInventoryRequests);

        // 총 금액
        List<ProductTotalAmountRequest> productTotalAmountRequests
                = orderRequests.stream()
                               .map(request ->
                                            new ProductTotalAmountRequest(request.productId(), request.quantity()))
                               .toList();
        BigDecimal totalPrice = productFinder.getTotalPrice(productTotalAmountRequests);

        // 쿠폰 생성 및 감소
        couponRegister.useMemberCoupon(couponId, member);

        // 포인트 감소
        BigDecimal discountedPrice = couponRegister.discountPrice(couponId, member, totalPrice);
        memberRegister.usePoint(memberId, discountedPrice);

        return OrderInfos.of(orderRequests, order, productFinder);
    }
}
