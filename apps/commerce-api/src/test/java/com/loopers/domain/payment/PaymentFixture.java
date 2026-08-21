package com.loopers.domain.payment;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import com.loopers.application.payment.PaymentRequest;

import org.instancio.Instancio;

import static org.instancio.Select.all;
import static org.instancio.Select.field;

public final class PaymentFixture {
    private static final Pattern CARD_NO_PATTERN = Pattern.compile("^\\d{4}-\\d{4}-\\d{4}-\\d{4}$");

    private PaymentFixture() {
    }

    public static Payments createPayment() {
        return Instancio.of(Payments.class)
                        .ignore(field(Payments::getId))
                        .generate(field(Payments::getOrderId), gen -> gen.string().alphaNumeric()
                                .minLength(1).maxLength(20))
                        .generate(field(Payments::getMemberId), gen -> gen.longs().range(1L, 100L))
                        .set(field(Payments::getTransactionKey), null)
                        .generate(field(Payments::getCardType), gen -> gen.enumOf(CardType.class))
                        .generate(field(Payments::getCardNo), gen -> gen.text()
                                .pattern("#d#d#d#d-#d#d#d#d-#d#d#d#d-#d#d#d#d"))
                        .generate(field(Payments::getTotalAmount), gen -> gen.math().bigDecimal()
                                .range(BigDecimal.ONE, BigDecimal.valueOf(100_000)).scale(0))
                        .generate(field(Payments::getPaymentType), gen -> gen.enumOf(PaymentType.class))
                        .set(field(Payments::getPaymentStatus), PaymentStatus.PENDING)
                        .supply(all(Pattern.class), () -> CARD_NO_PATTERN)
                        .create();
    }

    public static Payments createPayment(String orderId, Long memberId, String transactionKey,
                                         CardType cardType, String cardNo, BigDecimal totalAmount,
                                         PaymentType paymentType) {
        return Instancio.of(Payments.class)
                        .ignore(field(Payments::getId))
                        .set(field(Payments::getOrderId), orderId)
                        .set(field(Payments::getMemberId), memberId)
                        .set(field(Payments::getTransactionKey), transactionKey)
                        .set(field(Payments::getCardType), cardType)
                        .set(field(Payments::getCardNo), cardNo)
                        .set(field(Payments::getTotalAmount), totalAmount)
                        .set(field(Payments::getPaymentType), paymentType)
                        .set(field(Payments::getPaymentStatus), PaymentStatus.PENDING)
                        .supply(all(Pattern.class), () -> CARD_NO_PATTERN)
                        .create();
    }

    public static CreatePaymentSpec createPaymentSpec() {
        return Instancio.of(CreatePaymentSpec.class)
                        .generate(field(CreatePaymentSpec::orderId), gen -> gen.string().alphaNumeric().minLength(1).maxLength(20))
                        .generate(field(CreatePaymentSpec::memberId), gen -> gen.longs().range(1L, 100L))
                        .generate(field(CreatePaymentSpec::cardType), gen -> gen.enumOf(CardType.class))
                        .generate(field(CreatePaymentSpec::cardNo), gen -> gen.text()
                                .pattern("#d#d#d#d-#d#d#d#d-#d#d#d#d-#d#d#d#d"))
                        .generate(field(CreatePaymentSpec::totalAmount), gen -> gen.math().bigDecimal()
                                .range(BigDecimal.ONE, BigDecimal.valueOf(100_000)).scale(0))
                        .generate(field(CreatePaymentSpec::paymentType), gen -> gen.enumOf(PaymentType.class))
                        .set(field(CreatePaymentSpec::transactionKey), null)
                        .create();
    }

    public static CreatePaymentSpec createPaymentSpec(String orderId, Long memberId, CardType cardType,
                                                      String cardNo, BigDecimal totalAmount,
                                                      PaymentType paymentType) {
        return createPaymentSpec(orderId, memberId, null, cardType, cardNo, totalAmount, paymentType);
    }

    public static CreatePaymentSpec createPaymentSpec(String orderId, Long memberId, String transactionKey,
                                                      CardType cardType, String cardNo, BigDecimal totalAmount,
                                                      PaymentType paymentType) {
        return Instancio.of(CreatePaymentSpec.class)
                        .set(field(CreatePaymentSpec::orderId), orderId)
                        .set(field(CreatePaymentSpec::memberId), memberId)
                        .set(field(CreatePaymentSpec::transactionKey), transactionKey)
                        .set(field(CreatePaymentSpec::cardType), cardType)
                        .set(field(CreatePaymentSpec::cardNo), cardNo)
                        .set(field(CreatePaymentSpec::totalAmount), totalAmount)
                        .set(field(CreatePaymentSpec::paymentType), paymentType)
                        .create();
    }

    public static PaymentRequest createPaymentRequest(Long orderId) {
        return Instancio.of(PaymentRequest.class)
                        .set(field(PaymentRequest::orderId), orderId)
                        .generate(field(PaymentRequest::cardNo), gen -> gen.text()
                                .pattern("#d#d#d#d-#d#d#d#d-#d#d#d#d-#d#d#d#d"))
                        .generate(field(PaymentRequest::cardType), gen -> gen.enumOf(CardType.class))
                        .generate(field(PaymentRequest::totalAmount), gen -> gen.math().bigDecimal()
                                .range(BigDecimal.ONE, BigDecimal.valueOf(100_000)).scale(0))
                        .generate(field(PaymentRequest::paymentType), gen -> gen.enumOf(PaymentType.class))
                        .create();
    }

    public static PaymentRequest createPaymentRequest(Long orderId, String cardNo, CardType cardType,
                                                      BigDecimal totalAmount, PaymentType paymentType) {
        return Instancio.of(PaymentRequest.class)
                        .set(field(PaymentRequest::orderId), orderId)
                        .set(field(PaymentRequest::cardNo), cardNo)
                        .set(field(PaymentRequest::cardType), cardType)
                        .set(field(PaymentRequest::totalAmount), totalAmount)
                        .set(field(PaymentRequest::paymentType), paymentType)
                        .create();
    }
}
