package com.loopers.adapter.integration.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.TransactionDetailResponse;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.TransactionResponse;
import com.loopers.application.payment.PgPaymentRequest;

@FeignClient(name = "pg-client", url = "http://localhost:8082")
public interface PgFeignClient {

    @PostMapping("/api/v1/payments")
    ApiResponse<TransactionResponse> requestPayment(@RequestHeader("X-USER-ID") String memberId,
                                                    @RequestBody PgPaymentRequest paymentRequest);

    @GetMapping("/api/v1/payments/{transactionKey}")
    ApiResponse<TransactionDetailResponse> getPaymentStatus(@RequestHeader("X-USER-ID") String memberId,
                                                            @PathVariable String transactionKey);
}
