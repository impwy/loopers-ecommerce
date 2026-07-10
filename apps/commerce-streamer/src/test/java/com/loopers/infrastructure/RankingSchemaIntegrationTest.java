package com.loopers.infrastructure;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class RankingSchemaIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void rankingTablesAreCreatedOnMySql8() {
        assertThatCode(() -> {
            jdbcTemplate.queryForObject("SELECT COUNT(`rank`) FROM mv_product_rank_daily", Long.class);
            jdbcTemplate.queryForObject("SELECT COUNT(`rank`) FROM mv_product_rank_weekly", Long.class);
            jdbcTemplate.queryForObject("SELECT COUNT(`rank`) FROM mv_product_rank_monthly", Long.class);
        }).doesNotThrowAnyException();
    }
}
