package com.loopers.infrastructure.payment

import com.loopers.application.payment.TransactionInfo
import com.loopers.domain.payment.CardType
import com.loopers.domain.payment.TransactionStatus
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate

class PaymentCoreRelayTest {
    @Test
    fun `notify sends user ID header to callback URL`() {
        val restTemplate = RestTemplate()
        val mockServer = MockRestServiceServer.bindTo(restTemplate).build()
        val paymentCoreRelay = PaymentCoreRelay(restTemplate)
        val callbackUrl = "http://localhost:8080/api/v1/payments/pg-callback"
        val userId = "test-user"
        val transactionInfo = TransactionInfo(
            transactionKey = "transaction-key",
            orderId = "order-id",
            cardType = CardType.SAMSUNG,
            cardNo = "1234-1234-1234-1234",
            amount = 10_000L,
            status = TransactionStatus.SUCCESS,
            reason = null,
        )

        mockServer.expect(requestTo(callbackUrl))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("X-USER-ID", userId))
            .andRespond(withSuccess())

        paymentCoreRelay.notify(
            userId = userId,
            callbackUrl = callbackUrl,
            transactionInfo = transactionInfo,
        )

        mockServer.verify()
    }
}
