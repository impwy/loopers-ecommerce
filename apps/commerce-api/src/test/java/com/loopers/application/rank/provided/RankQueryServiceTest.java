package com.loopers.application.rank.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.loopers.application.rank.RankQueryService;
import com.loopers.adapter.integration.InMemoryRepository;
import com.loopers.application.product.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductBrandDomainService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.adapter.webapi.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

@ExtendWith(MockitoExtension.class)
class RankQueryServiceTest {
    @Mock
    private InMemoryRepository inMemoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductBrandDomainService productBrandDomainService;

    @InjectMocks
    private RankQueryService rankQueryService;

    @DisplayName("일간 랭킹은 요청 날짜의 Redis 키를 조회하고 문자열 productId 를 변환한다")
    @Test
    void get_daily_ranking_uses_request_date_and_parses_string_product_id() {
        LocalDate date = LocalDate.of(2026, 5, 10);
        Pageable pageable = PageRequest.of(0, 20);
        Product product = Product.create("상품", "상품입니다.", BigDecimal.valueOf(500),
                                         Brand.create("브랜드", "브랜드입니다."), ZonedDateTime.now());
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        ProductInfo productInfo = productInfo(1L);
        Set<TypedTuple<Object>> rankingTuples = typedTuples("1");

        when(inMemoryRepository.zReverRange("ranking:all:20260510", 0L, 99L))
                .thenReturn(rankingTuples);
        when(productRepository.findAllByIdIn(List.of(1L), pageable)).thenReturn(productPage);
        when(productBrandDomainService.findProductWithBrand(product, product.getBrand(), product.getLikeCount()))
                .thenReturn(productInfo);

        ProductInfoPageResponse response = rankQueryService.getDailyRanking(date, pageable);

        assertAll(
                () -> assertThat(response.content()).containsExactly(productInfo),
                () -> assertThat(response.totalElements()).isEqualTo(1)
        );
    }

    @DisplayName("주간 랭킹은 현재 날짜가 아니라 요청 날짜가 속한 주의 키를 조회한다")
    @Test
    void get_weekly_ranking_uses_request_date_week() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        Pageable pageable = PageRequest.of(0, 20);
        when(inMemoryRepository.zReverRange("ranking:weekly:2025-12-29_2026-01-04", 0L, 99L))
                .thenReturn(Set.of());
        when(productRepository.findAllByIdIn(List.of(), pageable)).thenReturn(Page.empty(pageable));

        rankQueryService.getWeeklyRanking(date, pageable);

        verify(inMemoryRepository).zReverRange("ranking:weekly:2025-12-29_2026-01-04", 0L, 99L);
    }

    private Set<TypedTuple<Object>> typedTuples(Object... values) {
        Set<TypedTuple<Object>> tuples = new LinkedHashSet<>();
        for (Object value : values) {
            tuples.add(typedTuple(value));
        }
        return tuples;
    }

    @SuppressWarnings("unchecked")
    private TypedTuple<Object> typedTuple(Object value) {
        TypedTuple<Object> typedTuple = mock(TypedTuple.class);
        when(typedTuple.getValue()).thenReturn(value);
        return typedTuple;
    }

    private ProductInfo productInfo(Long productId) {
        return new ProductInfo(productId, 1L, "상품", "상품입니다.", BigDecimal.valueOf(500),
                               "브랜드", "브랜드입니다.", ZonedDateTime.now(), ZonedDateTime.now(), 0L);
    }
}
