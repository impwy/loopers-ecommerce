package com.loopers.application.rank;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.loopers.application.provided.RankFinder;
import com.loopers.domain.rank.PeriodType;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.interfaces.api.rank.dto.RankingCriteria;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankFacade {
    private final RankFinder rankFinder;

    public ProductInfoPageResponse findProductRanking(RankingCriteria rankingCriteria) {
        PeriodType period = rankingCriteria.period();
        LocalDate date = rankingCriteria.date();
        Integer page = rankingCriteria.page();
        Integer size = rankingCriteria.size();
        Pageable pageable = Pageable.ofSize(size);
        pageable.withPage(page);
        switch (period) {
            case DAILY -> {return rankFinder.getDailyRanking(pageable);}
            case WEEKLY -> {return rankFinder.getWeeklyRanking(date, pageable);}
            case MONTHLY -> {return rankFinder.getMonthlyRanking(date, pageable);}
            default -> {return rankFinder.getDefaultRank(pageable);}
        }
    }
}
