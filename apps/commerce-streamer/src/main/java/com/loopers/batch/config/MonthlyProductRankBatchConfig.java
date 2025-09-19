package com.loopers.batch.config;

import java.time.LocalDate;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.loopers.batch.application.ProductRankProcessor;
import com.loopers.batch.domain.MvProductRankMonthly;
import com.loopers.batch.infrastructure.ProductRankDailyReader;
import com.loopers.batch.infrastructure.ProductRankWriter;
import com.loopers.domain.ranking.MvProductRankDaily;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class MonthlyProductRankBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ProductRankDailyReader reader;
    private final ProductRankProcessor processor;
    private final ProductRankWriter writer;

    @Bean
    public Job monthlyProductRankJob() {
        return new JobBuilder("monthlyRankJob", jobRepository)
                .start(monthlyRankStep())
                .build();
    }

    @Bean
    public Step monthlyRankStep() {
        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        JpaPagingItemReader<MvProductRankDaily> itemReader = reader.getPagingItemReader(startDate, endDate);

        return new StepBuilder("monthlyRankStep", jobRepository)
                .<MvProductRankDaily, MvProductRankMonthly>chunk(1000, transactionManager)
                .reader(itemReader)
                .processor(processor.processMonthly(startDate, endDate))
                .writer(writer.writeMonthly())
                .build();
    }
}
