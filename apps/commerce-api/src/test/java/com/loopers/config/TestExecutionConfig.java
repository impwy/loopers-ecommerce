package com.loopers.config;

import static org.mockito.Mockito.mock;

import java.util.concurrent.Executor;

import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;

import com.loopers.infrastructure.kafka.ProductEventProducer;

@Configuration
public class TestExecutionConfig {

    @Bean
    @Primary
    ProductEventProducer testProductEventProducer() {
        return mock(ProductEventProducer.class);
    }

    @Bean(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    @Primary
    Executor applicationTaskExecutor() {
        return new SyncTaskExecutor();
    }
}
