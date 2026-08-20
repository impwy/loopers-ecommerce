package com.loopers.adapter.webapi.rank;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.adapter.webapi.rank.dto.RankingCriteria;
import com.loopers.application.rank.provided.RankFinder;
import com.loopers.domain.rank.PeriodType;
import com.loopers.shared.stereotype.WebApiAdapter;

import lombok.RequiredArgsConstructor;

@WebApiAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/rankings")
public class RankV1ApiController implements RankV1ApiSpec {
    private final RankFinder rankFinder;

    @GetMapping
    @Override
    public ApiResponse<ProductInfoPageResponse> getProductRanking(@RequestParam String period,
                                                                  @RequestParam LocalDate date,
                                                                  @RequestParam Integer page,
                                                                  @RequestParam Integer size) {
        RankingCriteria rankingCriteria = new RankingCriteria(PeriodType.from(period), date, page, size);
        ProductInfoPageResponse productInfoWithRank = rankFinder.findProductRanking(rankingCriteria);
        return ApiResponse.success(productInfoWithRank);
    }
}
