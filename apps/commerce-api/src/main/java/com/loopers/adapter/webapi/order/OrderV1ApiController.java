package com.loopers.adapter.webapi.order;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.order.dto.OrderV1Dto;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderInfos;
import com.loopers.domain.member.UserId;
import com.loopers.shared.stereotype.WebApiAdapter;

import lombok.RequiredArgsConstructor;

@WebApiAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderV1ApiController implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @Override
    @PostMapping
    public ApiResponse<List<OrderV1Dto.Response.OrderInfo>> order(UserId userId, @RequestBody OrderV1Dto.Request.CreateOrderWithCouponRequest request) {
        OrderInfos orderInfos = orderFacade.order(userId, request);
        return ApiResponse.success(orderInfos.orderInfos());
    }
}
