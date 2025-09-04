package com.loopers.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.loopers.application.provided.ProductOutboxRegister;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxEventType;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
public class ProductOutboxRegisterIntegrationTest {

    @Autowired
    private ProductOutboxRegister productOutboxRegister;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 이벤트 outbox 생성 통합 테스트")
    @Test
    void create_product_event_outbox_test() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        CreateProductOutbox createProductOutbox = new CreateProductOutbox(1L, uuidString, ProductOutboxEventType.PRODUCT_LIKE_INCREMENT, 0L, ZonedDateTime.now());

        ProductEventOutbox expected = productOutboxRegister.register(createProductOutbox);

        assertAll(
                () -> assertThat(expected.getEventId()).isEqualTo(createProductOutbox.eventId()),
                () -> assertThat(expected.getEventType()).isEqualTo(createProductOutbox.eventType())
        );
    }
}
