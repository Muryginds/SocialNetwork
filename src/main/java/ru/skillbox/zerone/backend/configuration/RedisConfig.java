package ru.skillbox.zerone.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

@Configuration
public class RedisConfig {
  @Bean
  public RedisTemplate<Long, UUID> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<Long, UUID> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    return template;
  }
}
