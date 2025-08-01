package com.loopers.interfaces.api.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;

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
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.infrastructure.member.MemberJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.dto.PointV1Dto;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PointV1ApiE2ETest {
    private final TestRestTemplate restTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final MemberJpaRepository memberJpaRepository;

    @Autowired
    public PointV1ApiE2ETest(TestRestTemplate restTemplate, DatabaseCleanUp databaseCleanUp, MemberJpaRepository memberJpaRepository) {
        this.restTemplate = restTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.memberJpaRepository = memberJpaRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("GET /api/v1/points")
    @Nested
    class Get {
        public static final String ENDPOINT = "/api/v1/points";

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void returnPoint_whenGetPointSuccess() {
            Member member = memberJpaRepository.saveAndFlush(MemberFixture.createMember());

            ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointAmountResponse>> responseType =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointV1Dto.Response.PointAmountResponse>> response =
                    restTemplate.exchange(RequestEntity.get(ENDPOINT)
                                                           .header("X-USER-ID", member.getMemberId().memberId())
                                                           .build(),
                                              responseType);

            assertAll(
                    () -> assertThat(response.getBody().data().amount()).isEqualTo("0.00")
            );
        }
    }

    @DisplayName("POST /api/v1/points")
    @Nested
    class Post {
        public static final String ENDPOINT = "/api/v1/points/charge";

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnAmountWhenUserChargePoint() throws JsonProcessingException {
            Member member = memberJpaRepository.save(MemberFixture.createMember());
            String amount = "1000.00";
            BigDecimal expectedAmount = member.charge(new BigDecimal(amount));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-USER-ID", member.getMemberId().memberId());

            ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointAmountResponse>> responseType = new ParameterizedTypeReference<>() {};

            ResponseEntity<ApiResponse<PointV1Dto.Response.PointAmountResponse>> response =
                    restTemplate.exchange(ENDPOINT, HttpMethod.POST, new HttpEntity<>(amount, headers), responseType);

            assertAll(
                    () -> assertThat(response.getBody().data().amount()).isEqualTo(expectedAmount)
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void throwNotFoundExceptionWhenUserNotExist() {
            ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {};
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", "");
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<?> response =
                    restTemplate.exchange(ENDPOINT, HttpMethod.POST, new HttpEntity<>("1000", headers), responseType);

            System.out.println(response);

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }
}
