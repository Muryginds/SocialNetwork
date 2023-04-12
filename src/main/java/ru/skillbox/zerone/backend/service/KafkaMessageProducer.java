package ru.skillbox.zerone.backend.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.dto.request.MessageDTO;

@Component
public class KafkaMessageProducer {

  private final KafkaTemplate<String, MessageDTO> kafkaTemplate;

  @Value("${kafka.kafka-topic}")
  private String kafkaTopic;

  public KafkaMessageProducer(KafkaTemplate<String, MessageDTO> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(MessageDTO messageDTO){
    kafkaTemplate.send(kafkaTopic, messageDTO);
  }

}