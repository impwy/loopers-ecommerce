package com.loopers.domain.payment.paymentrule;

import com.loopers.domain.member.Member;
import com.loopers.domain.order.Order;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;

public interface PaymentService {
    void requestPayment(Order order, Member member, PaymentRequest payment);
}
