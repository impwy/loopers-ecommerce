package com.loopers.application.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.provided.PaymentRegister;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.PaymentRepository;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.payment.Payments;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class PaymentRegisterIntegrationTest {

    @MockitoSpyBean
    private PaymentRepository paymentRepository;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRegister paymentRegister;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("결제 생성 통합 테스트")
    @Test
    void create_payment_test() {
        Member member = memberRepository.save(MemberFixture.createMember());
        PaymentRequest paymentRequest = new PaymentRequest(member.getId(), "1111-2222-3333-4444", CardType.SAMSUNG, BigDecimal.TEN, PaymentType.CARD);

        Payments payment = paymentRegister.createPayment(1L, paymentRequest);

        assertAll(
                () -> assertThat(payment.getOrderId()).isEqualTo(1L),
                () -> assertThat(payment.getMemberId()).isEqualTo(member.getId()),
                () -> assertThat(payment.getCardType()).isEqualTo(CardType.SAMSUNG),
                () -> assertThat(payment.getCardNo()).isEqualTo(paymentRequest.cardNo())
        );
    }
}
