package com.loopers.batch.job;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.loopers.application.required.InMemoryRepository;
import com.loopers.batch.application.required.MvProductRankWeeklyRepository;
import com.loopers.batch.domain.MvProductRankWeekly;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeeklyInMemoryTaskLet implements Tasklet {
    private final MvProductRankWeeklyRepository weeklyRepository;
    private final InMemoryRepository inMemoryRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate startDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.with(DayOfWeek.SUNDAY);

        List<MvProductRankWeekly> rankings = weeklyRepository.findByStartDateAndEndDate(startDate, endDate);

        String key = "ranking:weekly:" + startDate + "_" + endDate;

        inMemoryRepository.delete(key);

        for (MvProductRankWeekly rank : rankings) {
            inMemoryRepository.saveZset(
                    key,
                    String.valueOf(rank.getProductId()),
                    rank.getScore(),
                    Duration.ofDays(8)
            );
        }

        return RepeatStatus.FINISHED;
    }
}
