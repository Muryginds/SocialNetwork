package ru.skillbox.zerone.backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaConfig {

  private final KafkaProperties kafkaProperties;

  @Autowired
  public Config(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    // get configs on application.properties/yml
    Map<String, Object> properties = kafkaProperties.buildProducerProperties();
    return new DefaultKafkaProducerFactory<>(properties);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public NewTopic topic() {
    return TopicBuilder
        .name("t.food.order")
        .partitions(1)
        .replicas(1)
        .build();
  }

}
