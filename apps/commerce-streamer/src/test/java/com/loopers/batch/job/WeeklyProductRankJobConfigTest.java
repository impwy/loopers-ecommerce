package com.loopers.batch.job;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.loopers.application.required.MvProductRankDailyRepository;
import com.loopers.batch.application.required.MvProductRankWeeklyRepository;
import com.loopers.batch.domain.MvProductRankWeekly;
import com.loopers.domain.ranking.MvProductRankDaily;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
@SpringBatchTest
@Import({
        WeeklyProductRankJobConfig.class,
        MonthlyProductRankJobConfig.class
})
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class WeeklyProductRankJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private Job weeklyProductRankJob;

    @Autowired
    private MvProductRankWeeklyRepository weeklyRepository;

    @Autowired
    private MvProductRankDailyRepository dailyRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(weeklyProductRankJob);

        LocalDate now = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);

        MvProductRankDaily d1 = MvProductRankDaily.create(1L, now, 100.0, 1);
        MvProductRankDaily d2 = MvProductRankDaily.create(2L, now, 80.0, 2);
        MvProductRankDaily d3 = MvProductRankDaily.create(3L, now, 50.0, 3);

        dailyRepository.saveAll(List.of(d1, d2, d3));
    }

    @Test
    void testWeeklyRankJob_savesWeeklyRankingToDb() throws Exception {
        // when
        JobExecution jobExecution = jobLauncherTestUtils.getJobLauncher()
                                                        .run(weeklyProductRankJob, new JobParametersBuilder()
                                                                .addLong("time", System.currentTimeMillis())
                                                                .toJobParameters());

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        List<MvProductRankWeekly> rankings = weeklyRepository.findAll();
        assertThat(rankings).hasSize(3);

        // 스코어와 랭킹 확인
        assertThat(rankings)
                .extracting(MvProductRankWeekly::getProductId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);

        assertThat(rankings.get(0).getStartDate()).isNotNull();
        assertThat(rankings.get(0).getEndDate()).isNotNull();
    }
}
