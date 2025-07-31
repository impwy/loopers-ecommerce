package com.loopers.application.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.OrderItemRegister;
import com.loopers.application.provided.OrderRegister;
import com.loopers.application.provided.ProductFinder;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;
import com.loopers.domain.orderitem.CreateOrderItemSpec;
import com.loopers.domain.product.Product;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Response.OrderProductInfo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderRegister orderRegister;
    private final MemberFinder memberFinder;
    private final ProductFinder productFinder;
    private final OrderItemRegister orderItemRegister;
    private final InventoryRegister inventoryRegister;

    @Transactional
    public List<OrderProductInfo> register(MemberId memberId, List<CreateOrderRequest> createOrderRequests) {
        List<OrderProductInfo> productInfos = new ArrayList<>();

        Member member = memberFinder.findByMemberId(memberId);
        CreateOrderSpec createOrderSpec = CreateOrderSpec.of(member.getId());

        Order order = orderRegister.register(createOrderSpec);
        Long orderId = order.getId();

        createOrderRequests.forEach(createOrderRequest -> {
            Product product = productFinder.find(createOrderRequest.productId());
            Long totalQuantity = createOrderRequest.quantity();
            BigDecimal quantity = BigDecimal.valueOf(totalQuantity);
            BigDecimal totalPrice = product.getTotalPrice(quantity);


            CreateOrderItemSpec createOrderItemSpec = CreateOrderItemSpec.of(orderId,
                                                                             createOrderRequest.productId(),
                                                                             totalQuantity,
                                                                             totalPrice);
            orderItemRegister.register(createOrderItemSpec);

            inventoryRegister.decrease(product.getId(), totalQuantity);
            member.decrease(totalPrice);

            OrderProductInfo orderProductInfo = OrderProductInfo.of(order.getOrderNo().value(), product.getName(),
                                                                    totalQuantity, totalPrice);
            productInfos.add(orderProductInfo);
        });

        return productInfos;
    }
}
