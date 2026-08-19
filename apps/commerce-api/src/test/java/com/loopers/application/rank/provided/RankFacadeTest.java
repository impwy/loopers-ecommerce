package com.loopers.application.rank.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.loopers.application.rank.RankFacade;
import com.loopers.domain.rank.PeriodType;
import com.loopers.adapter.webapi.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.adapter.webapi.rank.dto.RankingCriteria;

@ExtendWith(MockitoExtension.class)
class RankFacadeTest {
    @Mock
    private RankFinder rankFinder;

    @InjectMocks
    private RankFacade rankFacade;

    @DisplayName("랭킹 조회 시 요청한 페이지 번호와 크기를 반영한다")
    @Test
    void find_product_ranking_uses_requested_page() {
        LocalDate date = LocalDate.of(2026, 5, 10);
        RankingCriteria criteria = new RankingCriteria(PeriodType.DAILY, date, 2, 5);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(rankFinder.getDailyRanking(eq(date), pageableCaptor.capture()))
                .thenReturn(ProductInfoPageResponse.from(Page.empty(PageRequest.of(2, 5))));

        rankFacade.findProductRanking(criteria);

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        verify(rankFinder).getDailyRanking(eq(date), eq(pageable));
    }
}
