package com.loopers.batch.job;

import java.time.DayOfWeek;
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
import com.loopers.batch.domain.MvProductRankWeekly;
import com.loopers.batch.infrastructure.ProductRankDailyReader;
import com.loopers.batch.infrastructure.ProductRankWriter;
import com.loopers.domain.ranking.MvProductRankDaily;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class WeeklyProductRankJob {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ProductRankDailyReader reader;
    private final ProductRankProcessor processor;
    private final ProductRankWriter writer;
    private final WeeklyInMemoryTaskLet weeklyInMemoryTaskLet;

    @Bean
    public Job weeklyProductRankJob() {
        return new JobBuilder("weeklyRankJob", jobRepository)
                .start(weeklyRankStep())
                .next(weeklyRankInMemoryStep())
                .build();
    }

    @Bean
    public Step weeklyRankStep() {
        LocalDate startDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.with(DayOfWeek.SUNDAY);

        JpaPagingItemReader<MvProductRankDaily> itemReader = reader.getPagingItemReader(startDate, endDate);

        return new StepBuilder("weeklyRankStep", jobRepository)
                .<MvProductRankDaily, MvProductRankWeekly>chunk(1000, transactionManager)
                .reader(itemReader)
                .processor(processor.processWeekly(startDate, endDate))
                .writer(writer.writeWeekly())
                .build();
    }

    @Bean
    public Step weeklyRankInMemoryStep() {
        return new StepBuilder("weeklyRankInMemoryStep", jobRepository)
                .tasklet(weeklyInMemoryTaskLet, transactionManager)
                .build();
    }
}
