package com.loopers.application.payment.required;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.loopers.domain.payment.PaymentFixture;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.payment.Payments;
import com.loopers.support.BaseRepositoryTest;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class PaymentRepositoryTest extends BaseRepositoryTest {
    final PaymentRepository paymentRepository;

    @Test
    void saveAndFindByOrderId() {
        Payments payment = paymentRepository.save(createPayment("order-1", "transaction-1"));
        flushAndClear();

        Payments found = paymentRepository.findByOrderId("order-1").orElseThrow();

        assertAll(
                () -> assertThat(found.getId()).isEqualTo(payment.getId()),
                () -> assertThat(found.getTransactionKey()).isEqualTo("transaction-1"),
                () -> assertThat(found.getPaymentStatus()).isNotNull()
        );
    }

    @Test
    void saveAllAndFindAllByOrderId() {
        List<Payments> saved = paymentRepository.saveAll(List.of(
                createPayment("order-2", "transaction-1"),
                createPayment("order-2", "transaction-2")
        ));
        flushAndClear();

        List<Payments> found = paymentRepository.findAllByOrderId("order-2");

        assertThat(found).extracting(Payments::getTransactionKey)
                         .containsExactlyInAnyOrderElementsOf(saved.stream()
                                                                     .map(Payments::getTransactionKey)
                                                                     .toList());
    }

    private Payments createPayment(String orderId, String transactionKey) {
        return PaymentFixture.createPayment(orderId, 1L, transactionKey, CardType.SAMSUNG,
                                            "1234-1234-1234-1234", BigDecimal.TEN, PaymentType.CARD);
    }
}
