package com.loopers.application.provided;

import com.loopers.domain.member.MemberId;
import com.loopers.domain.payment.Payments;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;

public interface PaymentRegister {
    Payments createPayment(Long memberId, PaymentRequest paymentRequest);

    void requestPayment(MemberId memberId, PaymentRequest paymentRequest);
}
