package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderInfos;
import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.order.dto.OrderV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderV1ApiController implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @Override
    @PostMapping
    public ApiResponse<List<OrderV1Dto.Response.OrderInfo>> order(MemberId memberId, @RequestBody OrderV1Dto.Request.CreateOrderWithCouponRequest request) {
        OrderInfos orderInfos = orderFacade.order(memberId, request);
        return ApiResponse.success(orderInfos.getOrderInfos());
    }
}
