package com.loopers.adapter.webapi.order;

import java.util.List;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.order.dto.OrderV1Dto;
import com.loopers.application.order.CreateOrderWithCouponRequest;
import com.loopers.domain.member.UserId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order V1 API", description = "Order API 입니다.")
public interface OrderV1ApiSpec {

    @Operation(summary = "주문 생성", description = "상품을 주문합니다.")
    ApiResponse<List<OrderV1Dto.OrderInfo>> order(UserId userId, @RequestBody CreateOrderWithCouponRequest request);
}
