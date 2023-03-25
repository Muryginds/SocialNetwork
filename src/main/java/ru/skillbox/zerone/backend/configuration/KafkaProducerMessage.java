package ru.skillbox.zerone.backend.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.dto.request.MessageDTO;

@Component // this annotation inform Spring Boot that this class can be manage with the framework
public class KafkaProducerMessage {

  @Autowired
  private KafkaTemplate<String, MessageDTO> kafkaTemplate;

  private final String KAFKA_TOPIC = "message-topic";

  public void sendMessage(MessageDTO messageDTO){

    System.out.println("sendMessageProducer");
    kafkaTemplate.send(KAFKA_TOPIC, messageDTO);
  }

}