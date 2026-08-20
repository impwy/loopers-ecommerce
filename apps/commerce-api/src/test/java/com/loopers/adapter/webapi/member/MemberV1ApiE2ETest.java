package com.loopers.adapter.webapi.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.member.dto.MemberV1Dto;
import com.loopers.adapter.webapi.member.dto.MemberV1Dto.Request.MemberRegisterRequest;
import com.loopers.adapter.webapi.member.dto.MemberV1Dto.Response.MemberRegisterResponse;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.support.BaseApiTest;
import com.loopers.support.stereotype.WebApiAdapterTest;

@WebApiAdapterTest
class MemberV1ApiE2ETest extends BaseApiTest {
    private static final String ENDPOINT_POST = "/api/v1/members";
    private static final String ENDPOINT_GET = "/api/v1/members/me";

    @Nested
    class Post {
        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void register_member() {
            MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
            ParameterizedTypeReference<ApiResponse<MemberRegisterResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<MemberRegisterResponse>> result = restTestClient.post()
                    .uri(ENDPOINT_POST)
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
                    () -> assertThat(response.data().memberId()).isEqualTo(request.memberId()),
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
                    .uri(ENDPOINT_POST)
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
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.Response.MemberInfoResponse>> responseType =
                    new ParameterizedTypeReference<>() {};

            EntityExchangeResult<ApiResponse<MemberV1Dto.Response.MemberInfoResponse>> result = restTestClient.get()
                    .uri(ENDPOINT_GET)
                    .header("X-USER-ID", member.getMemberId().memberId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(responseType)
                    .returnResult();
            ApiResponse<MemberV1Dto.Response.MemberInfoResponse> response = result.getResponseBody();
            assertThat(response).isNotNull();
            assertThat(response.data()).isNotNull();

            assertAll(
                    () -> assertThat(response.data().id()).isEqualTo(member.getId()),
                    () -> assertThat(response.data().memberId()).isEqualTo(member.getMemberId().memberId()),
                    () -> assertThat(response.data().email()).isEqualTo(member.getEmail().email())
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void throwNotFoundException_whenMemberIdIsNotExist() {
            EntityExchangeResult<ApiResponse<Object>> result = restTestClient.get()
                    .uri(ENDPOINT_GET)
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
                    .uri(ENDPOINT_GET)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .returnResult();

            assertThat(result.getResponseBody()).isNotNull();
        }
    }
}
