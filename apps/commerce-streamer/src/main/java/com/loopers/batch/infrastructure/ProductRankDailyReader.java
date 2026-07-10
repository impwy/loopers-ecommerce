package com.loopers.batch.infrastructure;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.stereotype.Component;

import com.loopers.domain.ranking.MvProductRankDaily;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRankDailyReader {
    private final EntityManagerFactory entityManagerFactory;

    public JpaPagingItemReader<MvProductRankDaily> getPagingItemReader(LocalDate startDate, LocalDate endDate) {
        String query =
                "SELECT r FROM MvProductRankDaily r WHERE r.issuedDate BETWEEN :startDate AND :endDate ORDER BY r.productId";
        return new JpaPagingItemReaderBuilder<MvProductRankDaily>()
                .name("mvProductRankDailyReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(1000)
                .queryString(query)
                .parameterValues(Map.of("startDate", startDate, "endDate", endDate))
                .build();
    }
}
