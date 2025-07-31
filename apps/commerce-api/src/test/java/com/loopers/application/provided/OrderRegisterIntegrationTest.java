package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.OrderFacade;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.order.Address;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class OrderRegisterIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("존재하지 않는 유저 주문 시 실패")
    @Test
    void create_order_fail_when_user_not_existed() {
        CreateOrderRequest createOrderRequest
                = CreateOrderRequest.of(null, "000001",
                                        new Address("00001", "Seoul", "Seongdong-gu","Seuongsudong", "etc"));

        assertThatThrownBy(() -> orderFacade.register(createOrderRequest))
            .isInstanceOf(CoreException.class);
    }
}
