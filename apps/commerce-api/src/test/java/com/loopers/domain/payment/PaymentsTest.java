package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PaymentsTest {

    @DisplayName("잘못된 카드 번호 실패 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"fail", "abcd-efgh-ijkl-mnop"})
    void fail_when_card_number_is_wrong_test(String cardNo) {
        CreatePaymentSpec createPaymentSpec = new CreatePaymentSpec(1L, 1L, "paymentKey", "transactionKey",
                                                                    CardType.SAMSUNG, cardNo, BigDecimal.TEN,
                                                                    PaymentType.CARD, PaymentStatus.PENDING);

        assertThatThrownBy(() -> Payments.create(createPaymentSpec))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
