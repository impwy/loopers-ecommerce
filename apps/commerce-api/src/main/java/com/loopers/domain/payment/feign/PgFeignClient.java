package com.loopers.domain.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionResponse;

@FeignClient(name = "pg-client", url = "http://localhost:8082")
public interface PgFeignClient {

    @PostMapping("/api/v1/payments")
    ApiResponse<TransactionResponse> requestPayment(@RequestHeader("X-USER-ID") MemberId memberId,
                                                    @RequestBody PgPaymentRequest paymentRequest);

    @GetMapping("/api/v1/payments/{transactionKey}")
    ApiResponse<TransactionDetailResponse> getPaymentStatus(@RequestHeader("X-USER-ID") MemberId memberId,
                                                            @PathVariable String transactionKey);
}
