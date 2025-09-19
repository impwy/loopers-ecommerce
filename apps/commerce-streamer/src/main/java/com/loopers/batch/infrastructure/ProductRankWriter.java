package com.loopers.batch.infrastructure;

import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.stereotype.Component;

import com.loopers.batch.domain.MvProductRankMonthly;
import com.loopers.batch.domain.MvProductRankWeekly;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRankWriter {
    private final EntityManagerFactory entityManagerFactory;

    public JpaItemWriter<MvProductRankWeekly> writeWeekly() {
        return new JpaItemWriterBuilder<MvProductRankWeekly>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    public JpaItemWriter<MvProductRankMonthly> writeMonthly() {
        return new JpaItemWriterBuilder<MvProductRankMonthly>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
