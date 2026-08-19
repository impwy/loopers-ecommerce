package com.loopers.application.rank;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import com.loopers.application.rank.provided.RankFinder;
import com.loopers.application.required.InMemoryRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductBrandDomainService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankQueryService implements RankFinder {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Function<LocalDate, String> DAILY_RANKING_KEY =
            date -> "ranking:all:" + date.format(formatter);

    private final InMemoryRepository inMemoryRepository;
    private final ProductRepository productRepository;
    private final ProductBrandDomainService productBrandDomainService;

    @Override
    public ProductInfoPageResponse getDailyRanking(LocalDate date, Pageable pageable) {
        String key = DAILY_RANKING_KEY.apply(date);
        Set<TypedTuple<Object>> typedTuples = inMemoryRepository.zReverRange(key, 0L, 99L);
        List<Long> productIds = parseProductIds(typedTuples);
        return findRankedProducts(productIds, pageable);
    }

    @Override
    public ProductInfoPageResponse getWeeklyRanking(LocalDate date, Pageable pageable) {
        LocalDate startDate = date.with(DayOfWeek.MONDAY);
        LocalDate endDate = date.with(DayOfWeek.SUNDAY);
        String key = "ranking:weekly:" + startDate + "_" + endDate;

        Set<TypedTuple<Object>> typedTuples = inMemoryRepository.zReverRange(key, 0L, 99L);
        List<Long> productIds = parseProductIds(typedTuples);
        return findRankedProducts(productIds, pageable);
    }

    @Override
    public ProductInfoPageResponse getMonthlyRanking(LocalDate date, Pageable pageable) {
        LocalDate startDate = date.withDayOfMonth(1);
        String key = "ranking:monthly:" + startDate.getYear() + "_" + startDate.getMonthValue();

        Set<TypedTuple<Object>> typedTuples = inMemoryRepository.zReverRange(key, 0L, 99L);
        List<Long> productIds = parseProductIds(typedTuples);
        return findRankedProducts(productIds, pageable);
    }

    private ProductInfoPageResponse findRankedProducts(List<Long> productIds, Pageable pageable) {
        Page<Product> products = productRepository.findAllByIdIn(productIds, pageable);
        List<ProductInfo> list = products.stream()
                                         .map(product -> productBrandDomainService.findProductWithBrand(product,
                                                                                                        product.getBrand(),
                                                                                                        product.getLikeCount()))
                                         .toList();

        PageImpl<ProductInfo> page = new PageImpl<>(list, pageable, products.getTotalElements());
        return ProductInfoPageResponse.from(page);
    }

    private List<Long> parseProductIds(Set<TypedTuple<Object>> typedTuples) {
        if (typedTuples == null) {
            return List.of();
        }
        return typedTuples.stream()
                          .map(TypedTuple::getValue)
                          .map(this::parseProductId)
                          .toList();
    }

    private Long parseProductId(Object productId) {
        if (productId instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(productId));
    }

    @Override
    public ProductInfoPageResponse getDefaultRank(Pageable pageable) {
        Page<Product> products = productRepository.findAllOrderByLikeCountDesc(pageable);
        List<ProductInfo> list = products.stream()
                                         .map(product -> productBrandDomainService.findProductWithBrand(product,
                                                                                                        product.getBrand(),
                                                                                                        product.getLikeCount()))
                                         .toList();

        PageImpl<ProductInfo> page = new PageImpl<>(list, pageable, products.getTotalElements());
        return ProductInfoPageResponse.from(page);
    }
}
