package com.loopers.adapter.webapi.member;

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
import com.loopers.adapter.webapi.member.dto.MemberV1Dto;
import com.loopers.adapter.webapi.member.dto.MemberV1Dto.MemberRegisterResponse;
import com.loopers.application.member.MemberRegisterRequest;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.support.BaseApiTest;
import com.loopers.support.stereotype.WebApiAdapterTest;

@WebApiAdapterTest
class MemberV1ApiE2ETest extends BaseApiTest {
    private static final String MEMBER_GET = "/api/v1/members/me";
    private static final String MEMBER_POST = "/api/v1/members";
    private static final String POINT_GET = "/api/v1/members/points";
    private static final String POINT_CHARGE = "/api/v1/members/points/charge";

    @Nested
    class Post {
        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void register_member() {
            MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
            ParameterizedTypeReference<ApiResponse<MemberRegisterResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<MemberRegisterResponse>> result = restTestClient.post()
                                                                                             .uri(MEMBER_POST)
                                                                                             .contentType(MediaType.APPLICATION_JSON)
                                                                                             .body(request)
                                                                                             .exchange()
                                                                                             .expectStatus().isOk()
                                                                                             .expectBody(responseType)
                                                                                             .returnResult();
            ApiResponse<MemberRegisterResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();

            assertAll(
                    () -> assertThat(response.data().userId()).isEqualTo(request.memberId()),
                    () -> assertThat(response.data().emailAddress()).isEqualTo(request.email()),
                    () -> assertThat(response.data().gender()).isEqualTo(request.gender().name()),
                    () -> assertThat(response.data().birthday()).isEqualTo(request.birthday())
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throwBadRequest_whenGenderIsNull() {
            MemberRegisterRequest request = new MemberRegisterRequest("pwy6817", "secret", null,
                                                                      "pwy6817@loopers.app", "2025-07-13");

            EntityExchangeResult<ApiResponse<Object>> result = restTestClient.post()
                                                                             .uri(MEMBER_POST)
                                                                             .contentType(MediaType.APPLICATION_JSON)
                                                                             .body(request)
                                                                             .exchange()
                                                                             .expectStatus().isBadRequest()
                                                                             .expectBody(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                                                                             .returnResult();
            assertThat(result.getResponseBody()).isNotNull();
        }
    }

    @Nested
    class Get {
        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void get_memberInfo() {
            Member member = prepareMember();
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberInfoResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<MemberV1Dto.MemberInfoResponse>> result = restTestClient.get()
                                                                                                              .uri(MEMBER_GET)
                                                                                                              .header("X-USER-ID", member.getUserId().userId())
                                                                                                              .exchange()
                                                                                                              .expectStatus().isOk()
                                                                                                              .expectBody(responseType)
                                                                                                              .returnResult();
            ApiResponse<MemberV1Dto.MemberInfoResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();

            assertAll(
                    () -> assertThat(response.data().id()).isEqualTo(member.getId()),
                    () -> assertThat(response.data().memberId()).isEqualTo(member.getUserId().userId()),
                    () -> assertThat(response.data().email()).isEqualTo(member.getEmail().email())
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void throwNotFoundException_whenMemberIdIsNotExist() {
            EntityExchangeResult<ApiResponse<Object>> result = restTestClient.get()
                                                                             .uri(MEMBER_GET)
                                                                             .header("X-USER-ID", "")
                                                                             .exchange()
                                                                             .expectStatus().isNotFound()
                                                                             .expectBody(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                                                                             .returnResult();

            assertThat(result.getResponseBody()).isNotNull();
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throwBadRequestWhenX_USER_IDHeaderNotExist() {
            EntityExchangeResult<ApiResponse<Object>> result = restTestClient.get()
                                                                             .uri(MEMBER_GET)
                                                                             .exchange()
                                                                             .expectStatus().isBadRequest()
                                                                             .expectBody(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                                                                             .returnResult();

            assertThat(result.getResponseBody()).isNotNull();
        }

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void returnPoint_whenGetPointSuccess() {
            Member member = prepareMember();
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberWithPointResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<MemberV1Dto.MemberWithPointResponse>> result = restTestClient.get()
                                                                                                                   .uri(POINT_GET)
                                                                                                                   .header("X-USER-ID", member.getUserId().userId())
                                                                                                                   .exchange()
                                                                                                                   .expectStatus().isOk()
                                                                                                                   .expectBody(responseType)
                                                                                                                   .returnResult();
            ApiResponse<MemberV1Dto.MemberWithPointResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();

            assertThat(response.data().amount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnAmountWhenUserChargePoint() {
            Member member = prepareMember();
            String amount = "1000.00";
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberWithPointResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<MemberV1Dto.MemberWithPointResponse>> result = restTestClient.post()
                                                                                                                   .uri(POINT_CHARGE)
                                                                                                                   .header("X-USER-ID", member.getUserId().userId())
                                                                                                                   .contentType(MediaType.APPLICATION_JSON)
                                                                                                                   .body(amount)
                                                                                                                   .exchange()
                                                                                                                   .expectStatus().isOk()
                                                                                                                   .expectBody(responseType)
                                                                                                                   .returnResult();
            ApiResponse<MemberV1Dto.MemberWithPointResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();

            assertThat(response.data().amount()).isEqualByComparingTo(new BigDecimal(amount));
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void throwNotFoundExceptionWhenUserNotExist() {
            EntityExchangeResult<ApiResponse<Object>> result = restTestClient.post()
                                                                             .uri(POINT_CHARGE)
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
