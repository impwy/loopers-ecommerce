package com.loopers.interfaces.api.order;

import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.order.dto.OrderV1Dto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Order V1 API", description = "Order API 입니다.")
public interface OrderV1ApiSpec {

    @Operation(summary = "주문 생성", description = "상품을 주문합니다.")
    ApiResponse<List<OrderV1Dto.Response.OrderInfo>> order(MemberId memberId, @RequestBody OrderV1Dto.Request.CreateOrderWithCouponRequest request);
}
