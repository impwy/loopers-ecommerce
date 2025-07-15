package com.loopers.interfaces.api.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.infrastructure.MemberJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.PointV1Dto;
import com.loopers.interfaces.api.member.dto.PointV1Dto.Request.PointChargeRequest;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PointV1ApiE2ETest {
    private final TestRestTemplate restTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final MemberJpaRepository memberJpaRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public PointV1ApiE2ETest(TestRestTemplate restTemplate, DatabaseCleanUp databaseCleanUp, MemberJpaRepository memberJpaRepository,
                             ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.memberJpaRepository = memberJpaRepository;
        this.objectMapper = objectMapper;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/points")
    @Nested
    class Post {
        public static final Function<Long, String> ENDPOINT = id -> "/api/v1/points/charge/" + id;

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnAmountWhenUserChargePoint() throws JsonProcessingException {
            Member member = memberJpaRepository.save(MemberFixture.createMember());
            String amount = "1000";
            String endpointPost = ENDPOINT.apply(member.getId());

            PointChargeRequest pointChargeRequest = PointChargeRequest.of(member.getId(), amount);
            String request = objectMapper.writeValueAsString(pointChargeRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointAmountResponse>> responseType = new ParameterizedTypeReference<>() {};

            ResponseEntity<ApiResponse<PointV1Dto.Response.PointAmountResponse>> response =
                    restTemplate.exchange(endpointPost, HttpMethod.POST, new HttpEntity<>(request, headers), responseType);

            assertAll(
                    () -> assertThat(response.getBody().data().chargedAmount()).isEqualTo(amount)
            );
        }
    }

}
