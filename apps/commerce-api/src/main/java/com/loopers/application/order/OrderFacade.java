package com.loopers.application.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.OrderRegister;
import com.loopers.application.provided.ProductFinder;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;
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
    private final InventoryRegister inventoryRegister;

    @Transactional
    public List<OrderProductInfo> register(MemberId memberId, List<CreateOrderRequest> createOrderRequests) {
        List<OrderProductInfo> productInfos = new ArrayList<>();

        Member member = memberFinder.findByMemberId(memberId);
        CreateOrderSpec createOrderSpec = CreateOrderSpec.of(member.getId());

        Order order = orderRegister.register(createOrderSpec);

        createOrderRequests.forEach(createOrderRequest -> {
            Long requestQuantity = createOrderRequest.quantity();

            Long productId = createOrderRequest.productId();
            Product product = productFinder.find(productId);


            BigDecimal totalPrice = product.getTotalPrice(BigDecimal.valueOf(requestQuantity));

            order.addOrderItem(productId, requestQuantity, totalPrice);

            inventoryRegister.decrease(product.getId(), requestQuantity);

            member.decrease(totalPrice);

            OrderProductInfo orderProductInfo = OrderProductInfo.of(order.getOrderNo().value(), product.getName(),
                                                                    requestQuantity, totalPrice);

            productInfos.add(orderProductInfo);
        });

        return productInfos;
    }
}
