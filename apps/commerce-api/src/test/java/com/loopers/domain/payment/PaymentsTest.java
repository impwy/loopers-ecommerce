package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PaymentsTest {

    @DisplayName("잘못된 카드 번호 실패 테스트")
    @ParameterizedTest
    @ValueSource(strings = { "fail", "abcd-efgh-ijkl-mnop", "123-123-123-123" })
    void fail_when_card_number_is_wrong_test(String cardNo) {
        CreatePaymentSpec createPaymentSpec = new CreatePaymentSpec("orderId", 1L, "transactionKey",
                                                                    CardType.SAMSUNG, cardNo, BigDecimal.TEN,
                                                                    PaymentType.CARD);

        assertThatThrownBy(() -> Payments.create(createPaymentSpec))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("totalAmount null 실패 테스트")
    @Test
    void fail_when_totalAmount_is_null() {
        CreatePaymentSpec createPaymentSpec = new CreatePaymentSpec("orderId", 1L, "transactionKey", CardType.SAMSUNG,
                                                                    "1234-1234-1234-1234", null,
                                                                    PaymentType.CARD);

        assertThatThrownBy(() -> Payments.create(createPaymentSpec))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("결제 정보 생성 테스트")
    @Test
    void success_create_payment_test() {
        CreatePaymentSpec createPaymentSpec = new CreatePaymentSpec("orderId", 1L, "transactionKey",
                                                                    CardType.SAMSUNG, "1234-1234-1234-1234", BigDecimal.TEN,
                                                                    PaymentType.CARD);

        Payments expected = Payments.create(createPaymentSpec);

        assertAll(
                () -> assertThat(expected.getTransactionKey()).isEqualTo(createPaymentSpec.transactionKey()),
                () -> assertThat(expected.getPaymentType()).isEqualTo(createPaymentSpec.paymentType()),
                () -> assertThat(expected.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING)
        );
    }
}
