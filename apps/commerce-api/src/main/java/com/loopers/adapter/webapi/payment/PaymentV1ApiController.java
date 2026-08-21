package com.loopers.adapter.webapi.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.TransactionResponse;
import com.loopers.application.payment.PaymentCallbackRequest;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.application.payment.PaymentRequest;
import com.loopers.domain.member.UserId;
import com.loopers.shared.stereotype.WebApiAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebApiAdapter
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
        PaymentCallbackRequest callbackRequest = new PaymentCallbackRequest(transactionResponse.transactionKey(),
                                                                            transactionResponse.status(),
                                                                            transactionResponse.reason());
        paymentFacade.callback(userId, callbackRequest);
    }
}
