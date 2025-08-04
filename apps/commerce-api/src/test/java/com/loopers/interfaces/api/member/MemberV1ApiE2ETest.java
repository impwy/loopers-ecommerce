package com.loopers.interfaces.api.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.infrastructure.member.MemberJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.Request.MemberRegisterRequest;
import com.loopers.interfaces.api.member.dto.MemberV1Dto;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberV1ApiE2ETest {
    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final ObjectMapper objectMapper;
    private final MemberJpaRepository memberJpaRepository;

    @Autowired
    MemberV1ApiE2ETest(
            TestRestTemplate testRestTemplate,
            ObjectMapper objectMapper,
            DatabaseCleanUp databaseCleanUp,
            MemberJpaRepository memberJpaRepository
    ) {
        this.testRestTemplate = testRestTemplate;
        this.objectMapper = objectMapper;
        this.databaseCleanUp = databaseCleanUp;
        this.memberJpaRepository = memberJpaRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    class Post {
        private static final String ENDPOINT_POST = "/api/v1/members";

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void register_member() throws JsonProcessingException {
            MemberRegisterRequest memberRegisterRequest = MemberFixture.createMemberRegisterRequest();
            String memberRegisterJson = objectMapper.writeValueAsString(memberRegisterRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ParameterizedTypeReference<ApiResponse<MemberV1Dto.Response.MemberRegisterResponse>> responseType =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberV1Dto.Response.MemberRegisterResponse>> response =
                    testRestTemplate.exchange(ENDPOINT_POST,
                                              HttpMethod.POST,
                                              new HttpEntity<>(memberRegisterJson, headers),
                                              responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().memberId()).isEqualTo(memberRegisterRequest.memberId()),
                    () -> assertThat(response.getBody().data().emailAddress()).isEqualTo(memberRegisterRequest.email()),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(memberRegisterRequest.gender().name()),
                    () -> assertThat(response.getBody().data().birthday()).isEqualTo(memberRegisterRequest.birthday())
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throwBadRequest_whenGenderIsNull() throws JsonProcessingException {
            MemberRegisterRequest memberRegister = new MemberRegisterRequest("pwy6817", "secret", null,
                                                           "pwy6817@loopers.app", "2025-07-13");
            String memberRegisterJson = objectMapper.writeValueAsString(memberRegister);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ParameterizedTypeReference<ApiResponse<MemberV1Dto.Response.MemberRegisterResponse>> responseType =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberV1Dto.Response.MemberRegisterResponse>> response =
                    testRestTemplate.exchange(ENDPOINT_POST,
                                              HttpMethod.POST,
                                              new HttpEntity<>(memberRegisterJson, headers),
                                              responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }

    @Nested
    class Get {
        private static final String ENDPOINT_GET = "/api/v1/members/me";

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void get_memberInfo() {
            Member member = memberJpaRepository.saveAndFlush(MemberFixture.createMember());

            ParameterizedTypeReference<ApiResponse<MemberV1Dto.Response.MemberInfoResponse>> responseType =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberV1Dto.Response.MemberInfoResponse>> response =
                    testRestTemplate.exchange(RequestEntity.get(ENDPOINT_GET)
                                                           .header("X-USER-ID", member.getMemberId().memberId())
                                                           .build(),
                                              responseType);

            assertAll(
                    () -> assertThat(response.getBody().data().id()).isEqualTo(1L),
                    () -> assertThat(response.getBody().data().memberId()).isEqualTo(member.getMemberId().memberId()),
                    () -> assertThat(response.getBody().data().email()).isEqualTo(member.getEmail().email())
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void throwNotFoundException_whenMemberIdIsNotExist() {

            ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<?> response =
                    testRestTemplate.exchange(RequestEntity.get(ENDPOINT_GET)
                                                           .header("X-USER-ID", "")
                                                           .build(),
                                              responseType);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throwBadRequestWhenX_USER_IDHeaderNotExist() {
            ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<?> response =
                    testRestTemplate.exchange(ENDPOINT_GET, HttpMethod.GET, new HttpEntity<>(null), responseType);

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }
}
