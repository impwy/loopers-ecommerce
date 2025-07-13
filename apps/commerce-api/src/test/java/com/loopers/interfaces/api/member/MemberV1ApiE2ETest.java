package com.loopers.interfaces.api.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.MemberFixture;
import com.loopers.domain.MemberRegisterRequest;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.MemberRegisterResponse;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberV1ApiE2ETest {
    private static final String ENDPOINT_POST = "/api/members";

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final ObjectMapper objectMapper;

    @Autowired
    MemberV1ApiE2ETest(
            TestRestTemplate testRestTemplate,
            ObjectMapper objectMapper,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.testRestTemplate = testRestTemplate;
        this.objectMapper = objectMapper;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    class Post {
        @Test
        void register_member() throws JsonProcessingException {
            MemberRegisterRequest memberRegisterRequest = MemberFixture.createMemberRegisterRequest();
            String memberRegisterJson = objectMapper.writeValueAsString(memberRegisterRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ParameterizedTypeReference<ApiResponse<MemberRegisterResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberRegisterResponse>> response =
                    testRestTemplate.exchange(ENDPOINT_POST,
                                              HttpMethod.POST,
                                              new HttpEntity<>(memberRegisterJson, headers),
                                              responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().memberId()).isEqualTo(memberRegisterRequest.memberId()),
                    () -> assertThat(response.getBody().data().emailAddress()).isEqualTo(memberRegisterRequest.email()),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(memberRegisterRequest.gender().name()),
                    () -> assertThat(response.getBody().data().birthday()).isEqualTo(memberRegisterRequest.birthDay())
            );
        }

        @Test
        void throwBadRequest_whenGenderIsNull() throws JsonProcessingException {
            MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("pwy6817", "secret", null, "pwy6817@loopers.app", "2025-07-13");
            String memberRegisterJson = objectMapper.writeValueAsString(memberRegisterRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ParameterizedTypeReference<ApiResponse<MemberRegisterResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberRegisterResponse>> response =
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
}
