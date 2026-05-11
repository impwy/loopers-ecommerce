package com.loopers.batch.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.transaction.PlatformTransactionManager;

import com.loopers.batch.application.ProductRankProcessor;
import com.loopers.batch.domain.MvProductRankMonthly;
import com.loopers.batch.infrastructure.ProductRankDailyReader;
import com.loopers.batch.infrastructure.ProductRankWriter;
import com.loopers.domain.ranking.MvProductRankDaily;

@ExtendWith(MockitoExtension.class)
class MonthlyProductRankJobConfigTest {
    @Mock
    private JobRepository jobRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private ProductRankDailyReader reader;

    @Mock
    private ProductRankProcessor processor;

    @Mock
    private ProductRankWriter writer;

    @Mock
    private MonthlyInMemoryTaskLet monthlyInMemoryTaskLet;

    @DisplayName("월간 랭킹 Job은 DB 집계 후 Redis 적재 Step을 실행한다")
    @Test
    void monthly_product_rank_job_contains_in_memory_step() {
        MonthlyProductRankJobConfig config = new MonthlyProductRankJobConfig(jobRepository, transactionManager,
                                                                             reader, processor, writer,
                                                                             monthlyInMemoryTaskLet);
        when(reader.getPagingItemReader(any(), any())).thenReturn(new JpaPagingItemReader<MvProductRankDaily>());
        when(processor.processMonthly(any(), any())).thenReturn(item -> null);
        when(writer.writeMonthly()).thenReturn(mockJpaItemWriter());

        Job job = config.monthlyProductRankJob();

        assertThat(((SimpleJob) job).getStepNames())
                .containsExactly("monthlyRankStep", "monthlyInMemoryStep");
    }

    @SuppressWarnings("unchecked")
    private JpaItemWriter<MvProductRankMonthly> mockJpaItemWriter() {
        return (JpaItemWriter<MvProductRankMonthly>) org.mockito.Mockito.mock(JpaItemWriter.class);
    }
}
