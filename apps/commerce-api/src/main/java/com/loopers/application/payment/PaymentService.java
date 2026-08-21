package com.loopers.application.payment;

import com.loopers.domain.member.Member;
import com.loopers.domain.order.Order;

public interface PaymentService {
    void requestPayment(Order order, Member member, PaymentRequest payment);
}
