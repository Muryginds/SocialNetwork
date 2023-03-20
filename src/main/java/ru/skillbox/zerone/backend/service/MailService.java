package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.skillbox.zerone.backend.configuration.KafkaProducerMessage;
import ru.skillbox.zerone.backend.model.dto.request.MessageDTO;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;



@Service
@RequiredArgsConstructor
public class MailService {

  private static final String ACCOUNT_CONFIRMATION_MESSAGE_THEME = "Подтверждение аккаунта";
  private static final String EMAIL_CONFIRMATION_MESSAGE_THEME = "Подтверждение смены пароля";
  @Autowired
  private KafkaProducerMessage kafkaProducerMessage;

  MessageDTO messageDto;


  public void sendVerificationEmail(String email, String verifyCode, String pathUri, String hostAddress) {

    messageDto = new MessageDTO().setEmail(email).setTHEME(ACCOUNT_CONFIRMATION_MESSAGE_THEME)
        .setVerificationLink(MessageFormatter.format("Пожалуйста, подтвердите ваш аккаунт, перейдя по ссылке: {}",   //вот это и передать
            createVerificationLink(email, verifyCode, pathUri, hostAddress)).getMessage());

//    var message = createVerificationMessage(
//        email,
//        ACCOUNT_CONFIRMATION_MESSAGE_THEME,
//        MessageFormatter.format("Пожалуйста, подтвердите ваш аккаунт, перейдя по ссылке: {}", //вот это и передать
//            createVerificationLink(email, verifyCode, pathUri, hostAddress)).getMessage()
    //);

    kafkaProducerMessage.sendMessage(messageDto);
    //emailSender.send(message);
  }

  public void sendVerificationChangeEmail(String email, String verifyCode, String pathUri, String hostAddress) {

     messageDto = new MessageDTO().setEmail(email).setTHEME(EMAIL_CONFIRMATION_MESSAGE_THEME)
        .setVerificationLink(MessageFormatter.format("Пожалуйста, подтвердите смену email, перейдя по ссылке: {}",   //вот это и передать
            createVerificationLink(email, verifyCode, pathUri, hostAddress)).getMessage());
//    var message = createVerificationMessage(
//        email,
//        EMAIL_CONFIRMATION_MESSAGE_THEME,
//        MessageFormatter.format("Пожалуйста, подтвердите смену email, перейдя по ссылке: {}",   //вот это и передать
//            createVerificationLink(email, verifyCode, pathUri, hostAddress)).getMessage()
//    );

    kafkaProducerMessage.sendMessage(messageDto);

    //emailSender.send(message);
  }

  private String createVerificationLink(String userId, String token, String path, String siteAddress) {
    return UriComponentsBuilder
        .fromHttpUrl(siteAddress)
        .path(path)
        .queryParam("userId", URLEncoder.encode(userId, StandardCharsets.UTF_8))
        .queryParam("token", URLEncoder.encode(token, StandardCharsets.UTF_8))
        .build()
        .toUriString();
  }
}
