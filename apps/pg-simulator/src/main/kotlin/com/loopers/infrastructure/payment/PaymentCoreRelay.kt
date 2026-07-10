package com.loopers.infrastructure.payment

import com.loopers.application.payment.TransactionInfo
import com.loopers.domain.payment.PaymentRelay
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PaymentCoreRelay(
    private val restTemplate: RestTemplate,
) : PaymentRelay {
    constructor() : this(RestTemplate())

    companion object {
        private val logger = LoggerFactory.getLogger(PaymentCoreRelay::class.java)
        private const val USER_ID_HEADER = "X-USER-ID"
    }

    override fun notify(userId: String, callbackUrl: String, transactionInfo: TransactionInfo) {
        runCatching {
            val headers = HttpHeaders().apply { set(USER_ID_HEADER, userId) }
            val request = HttpEntity(transactionInfo, headers)
            restTemplate.postForEntity(callbackUrl, request, Any::class.java)
        }.onFailure { e -> logger.error("콜백 호출을 실패했습니다. {}", e.message, e) }
    }
}
