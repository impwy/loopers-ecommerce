package com.loopers.interfaces.api.payment;

import org.springframework.web.bind.annotation.RequestBody;

import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "payment V1 API", description = "결제 관련 API 입니다")
public interface PaymentV1ApiSpec {

    @Operation(
            summary = "결제 요청",
            description = "결제를 요청한다."
    )
    void pay(MemberId memberId, @RequestBody PaymentRequest paymentRequest);

    @Operation(
            summary = "결제 콜백",
            description = "결제 콜백을 받는다."
    )
    void callback(TransactionResponse transactionResponse);
}
