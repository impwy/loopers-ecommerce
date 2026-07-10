package com.loopers.batch.job;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.loopers.application.required.InMemoryRepository;
import com.loopers.batch.application.required.MvProductRankMonthlyRepository;
import com.loopers.batch.domain.MvProductRankMonthly;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MonthlyInMemoryTaskLet implements Tasklet {
    private final MvProductRankMonthlyRepository monthlyRepository;
    private final InMemoryRepository inMemoryRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<MvProductRankMonthly> rankings = monthlyRepository.findByStartDateAndEndDate(startDate, endDate);

        String key = "ranking:monthly:" + startDate.getYear() + "_" + startDate.getMonthValue();

        inMemoryRepository.delete(key);

        for (MvProductRankMonthly rank : rankings) {
            inMemoryRepository.saveZset(
                    key,
                    String.valueOf(rank.getProductId()),
                    rank.getScore(),
                    Duration.ofDays(32)
            );
        }

        return RepeatStatus.FINISHED;
    }
}
