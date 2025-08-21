package com.loopers.interfaces.api.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentV1ApiController implements PaymentV1ApiSpec {
    private final PaymentFacade paymentFacade;

    @PostMapping("/pay")
    @Override
    public void pay(MemberId memberId, @RequestBody PaymentRequest paymentRequest) {
        paymentFacade.requestPayment(memberId, paymentRequest);
    }

    @PostMapping("/pg-callback")
    @Override
    public void callback(@RequestBody TransactionResponse transactionResponse) {
        log.info("callback : {}", transactionResponse);
    }
}
