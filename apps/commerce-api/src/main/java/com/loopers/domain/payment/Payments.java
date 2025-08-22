package com.loopers.domain.payment;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payments extends BaseEntity {

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "transaction_key")
    private String transactionKey;

    @Column(name = "card_type")
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private final Pattern REGEX_CARD_NO = Pattern.compile("^\\d{4}-\\d{4}-\\d{4}-\\d{4}$");

    private Payments(String orderId, Long memberId, String transactionKey, CardType cardType, String cardNo,
                     BigDecimal totalAmount, PaymentType paymentType) {
        this.orderId = requireNonNull(orderId);
        this.memberId = requireNonNull(memberId);
        this.transactionKey = transactionKey;
        this.cardType = cardType;
        if (!REGEX_CARD_NO.matcher(cardNo).matches()) {
            throw new IllegalArgumentException("잘못된 카드 번호 입니다.");
        }
        this.cardNo = cardNo;
        this.totalAmount = requireNonNull(totalAmount);
        this.paymentType = paymentType;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public static Payments create(CreatePaymentSpec createPaymentSpec) {
        return new Payments(
                createPaymentSpec.orderId(),
                createPaymentSpec.memberId(),
                createPaymentSpec.transactionKey(),
                createPaymentSpec.cardType(),
                createPaymentSpec.cardNo(),
                createPaymentSpec.totalAmount(),
                createPaymentSpec.paymentType()
        );
    }

    public void successPayments() {
        this.paymentStatus = PaymentStatus.SUCCESS;
    }

    public void failPayments() {
        this.paymentStatus = PaymentStatus.FAILED;
    }
}
