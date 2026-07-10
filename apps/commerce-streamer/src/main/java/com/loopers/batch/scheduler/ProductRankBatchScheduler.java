package com.loopers.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ProductRankBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job weeklyProductRankJob;
    private final Job monthlyProductRankJob;

    @Scheduled(cron = "0 0 1 ? * MON")
    public void runWeeklyProductRankJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(weeklyProductRankJob, jobParameters);
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void runMonthlyProductRankJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(monthlyProductRankJob, jobParameters);
    }
}
