package com.loopers.interfaces.api.rank;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.rank.RankFacade;
import com.loopers.domain.rank.PeriodType;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.interfaces.api.rank.dto.RankingCriteria;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rankings")
public class RankV1ApiController implements RankV1ApiSpec {
    private final RankFacade rankFacade;

    @GetMapping
    @Override
    public ApiResponse<ProductInfoPageResponse> getProductRanking(@RequestParam String period,
                                                                  @RequestParam LocalDate date,
                                                                  @RequestParam Integer page,
                                                                  @RequestParam Integer size) {
        RankingCriteria rankingCriteria = new RankingCriteria(PeriodType.from(period), date, page, size);
        ProductInfoPageResponse productInfoWithRank = rankFacade.findProductRanking(rankingCriteria);
        return ApiResponse.success(productInfoWithRank);
    }
}
