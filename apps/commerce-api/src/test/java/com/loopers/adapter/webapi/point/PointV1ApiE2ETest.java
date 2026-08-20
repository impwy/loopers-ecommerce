package com.loopers.adapter.webapi.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.point.dto.PointV1Dto;
import com.loopers.domain.member.Member;
import com.loopers.support.BaseApiTest;
import com.loopers.support.stereotype.WebApiAdapterTest;

@WebApiAdapterTest
class PointV1ApiE2ETest extends BaseApiTest {
    private static final String ENDPOINT_GET = "/api/v1/points";
    private static final String ENDPOINT_CHARGE = "/api/v1/points/charge";

    @DisplayName("GET /api/v1/points")
    @Nested
    class Get {
        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void returnPoint_whenGetPointSuccess() {
            Member member = prepareMember();
            ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointAmountResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<PointV1Dto.Response.PointAmountResponse>> result = restTestClient.get()
                    .uri(ENDPOINT_GET)
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<PointV1Dto.Response.PointAmountResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();

            assertThat(response.data().amount()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @DisplayName("POST /api/v1/points")
    @Nested
    class Post {
        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnAmountWhenUserChargePoint() {
            Member member = prepareMember();
            String amount = "1000.00";
            ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointAmountResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<PointV1Dto.Response.PointAmountResponse>> result = restTestClient.post()
                    .uri(ENDPOINT_CHARGE)
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(amount)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<PointV1Dto.Response.PointAmountResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();

            assertThat(response.data().amount()).isEqualByComparingTo(new BigDecimal(amount));
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void throwNotFoundExceptionWhenUserNotExist() {
            EntityExchangeResult<ApiResponse<Object>> result = restTestClient.post()
                    .uri(ENDPOINT_CHARGE)
                    .header("X-USER-ID", "")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("1000")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .returnResult();

            assertAll(
                    () -> assertThat(result.getResponseBody()).isNotNull()
            );
        }
    }
}
