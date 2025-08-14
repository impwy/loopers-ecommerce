package com.loopers.application.redis;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
            .withExposedPorts(6379)
            .withCommand("redis-server --requirepass yong");

    @BeforeAll
    static void setUpRedisConnectionFactory(@Autowired RedisTemplate<String, String> redisTemplate) {
        String address = redis.getHost();
        Integer port = redis.getMappedPort(6379);

        // RedisTemplate이 Testcontainers의 Redis를 바라보도록 설정
        LettuceConnectionFactory connectionFactory =
                new LettuceConnectionFactory(address, port);
        connectionFactory.setPassword("yong"); // 비밀번호 설정
        connectionFactory.afterPropertiesSet();

        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
    }

    @Test
    void redis_test() {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set("foo", "bar");

        String value = operations.get("foo");

        assertThat(value).isEqualTo("bar");
    }
}
