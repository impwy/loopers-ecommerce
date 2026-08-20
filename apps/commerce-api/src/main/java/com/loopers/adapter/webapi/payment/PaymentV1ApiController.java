package com.loopers.adapter.webapi.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.member.UserId;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Response.TransactionResponse;

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
    public void pay(UserId userId, @RequestBody PaymentRequest paymentRequest) {
        paymentFacade.requestPayment(userId, paymentRequest);
    }

    @PostMapping("/pg-callback")
    @Override
    public void callback(UserId userId, @RequestBody TransactionResponse transactionResponse) {
        paymentFacade.callback(userId, transactionResponse);
    }
}
