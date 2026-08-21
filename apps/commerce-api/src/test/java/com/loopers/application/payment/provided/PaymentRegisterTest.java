package com.loopers.application.payment.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.loopers.domain.member.Member;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.payment.Payments;
import com.loopers.application.payment.PaymentRequest;
import com.loopers.domain.payment.PaymentFixture;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationServiceTest;

@ApplicationServiceTest
class PaymentRegisterTest extends BaseApplicationServiceTest {

    @Autowired
    private PaymentRegister paymentRegister;

    @DisplayName("결제 생성 통합 테스트")
    @Test
    void create_payment_test() {
        Member member = prepareMember();
        Order order = prepareOrder(member.getId());
        PaymentRequest paymentRequest = PaymentFixture.createPaymentRequest(order.getId(),
                                                                            "1111-2222-3333-4444",
                                                                            CardType.SAMSUNG,
                                                                            BigDecimal.TEN,
                                                                            PaymentType.CARD);

        Payments payment = paymentRegister.createPayment(member.getId(), paymentRequest);

        assertAll(
                () -> assertThat(payment.getOrderId()).isEqualTo(order.getOrderNo().value()),
                () -> assertThat(payment.getMemberId()).isEqualTo(member.getId()),
                () -> assertThat(payment.getCardType()).isEqualTo(CardType.SAMSUNG),
                () -> assertThat(payment.getCardNo()).isEqualTo(paymentRequest.cardNo())
        );
    }
}
