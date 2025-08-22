package com.loopers.domain.payment.paymentrule;

import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;

public interface PaymentService {
    void requestPayment(String orderId, MemberId memberId, PaymentRequest payment);
}
