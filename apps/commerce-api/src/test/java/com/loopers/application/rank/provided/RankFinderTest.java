package com.loopers.application.rank.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.loopers.shared.InMemoryRepository;
import com.loopers.adapter.webapi.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.adapter.webapi.rank.dto.RankingCriteria;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.rank.PeriodType;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationServiceTest
@RequiredArgsConstructor
class RankFinderTest extends BaseApplicationServiceTest {
    @MockitoBean
    private InMemoryRepository inMemoryRepository;

    final RankFinder rankFinder;

    Brand brand;
    Product product;

    @BeforeEach
    void setUp() {
        brand = prepareBrand();
        product = prepareProduct();
    }

    @DisplayName("일간 랭킹은 요청 날짜의 Redis 키를 조회하고 문자열 productId 를 변환한다")
    @Test
    void get_daily_ranking_uses_request_date_and_parses_string_product_id() {
        LocalDate date = LocalDate.of(2026, 5, 10);
        Pageable pageable = PageRequest.of(0, 20);

        when(inMemoryRepository.zReverRange("ranking:all:20260510", 0L, 99L))
                .thenReturn(typedTuples(product.getId().toString()));

        ProductInfoPageResponse response = rankFinder.getDailyRanking(date, pageable);

        assertAll(
                () -> assertThat(response.content()).hasSize(1),
                () -> assertThat(response.content().get(0).productId()).isEqualTo(product.getId()),
                () -> assertThat(response.content().get(0).brandId()).isEqualTo(brand.getId()),
                () -> assertThat(response.totalElements()).isEqualTo(1),
                () -> verify(inMemoryRepository).zReverRange("ranking:all:20260510", 0L, 99L)
        );
    }

    @DisplayName("주간 랭킹은 현재 날짜가 아니라 요청 날짜가 속한 주의 키를 조회한다")
    @Test
    void get_weekly_ranking_uses_request_date_week() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        Pageable pageable = PageRequest.of(0, 20);
        when(inMemoryRepository.zReverRange("ranking:weekly:2025-12-29_2026-01-04", 0L, 99L))
                .thenReturn(Set.of());

        ProductInfoPageResponse response = rankFinder.getWeeklyRanking(date, pageable);

        assertAll(
                () -> assertThat(response.content()).isEmpty(),
                () -> assertThat(response.totalElements()).isZero(),
                () -> verify(inMemoryRepository).zReverRange("ranking:weekly:2025-12-29_2026-01-04", 0L, 99L)
        );
    }

    @DisplayName("월간 랭킹은 요청 날짜가 속한 월의 Redis 키를 조회한다")
    @Test
    void get_monthly_ranking_uses_request_month_key() {
        LocalDate date = LocalDate.of(2026, 5, 31);
        Pageable pageable = PageRequest.of(0, 20);

        when(inMemoryRepository.zReverRange("ranking:monthly:2026_5", 0L, 99L))
                .thenReturn(typedTuples(product.getId()));

        ProductInfoPageResponse response = rankFinder.getMonthlyRanking(date, pageable);

        assertAll(
                () -> assertThat(response.content()).hasSize(1),
                () -> assertThat(response.content().get(0).productId()).isEqualTo(product.getId()),
                () -> assertThat(response.totalElements()).isEqualTo(1),
                () -> verify(inMemoryRepository).zReverRange("ranking:monthly:2026_5", 0L, 99L)
        );
    }

    @DisplayName("랭킹 조회는 요청한 페이지 번호와 크기를 실제 상품 조회에 반영한다")
    @Test
    void find_product_ranking_uses_requested_page_and_size() {
        LocalDate date = LocalDate.of(2026, 5, 10);
        RankingCriteria criteria = new RankingCriteria(PeriodType.DAILY, date, 1, 2);
        Product firstProduct = prepareProduct();
        Product secondProduct = prepareProduct();
        Product thirdProduct = prepareProduct();

        when(inMemoryRepository.zReverRange("ranking:all:20260510", 0L, 99L))
                .thenReturn(typedTuples(firstProduct.getId(), secondProduct.getId(), thirdProduct.getId()));

        ProductInfoPageResponse response = rankFinder.findProductRanking(criteria);

        assertAll(
                () -> assertThat(response.pageNumber()).isEqualTo(1),
                () -> assertThat(response.pageSize()).isEqualTo(2),
                () -> assertThat(response.totalElements()).isEqualTo(3),
                () -> assertThat(response.content()).hasSize(1),
                () -> verify(inMemoryRepository).zReverRange("ranking:all:20260510", 0L, 99L)
        );
    }

    private Set<TypedTuple<Object>> typedTuples(Object... values) {
        Set<TypedTuple<Object>> tuples = new LinkedHashSet<>();
        for (int index = 0; index < values.length; index++) {
            tuples.add(new DefaultTypedTuple<>(values[index], (double) (values.length - index)));
        }
        return tuples;
    }
}
