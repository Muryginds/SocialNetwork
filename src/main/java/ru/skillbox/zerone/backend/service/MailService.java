package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.skillbox.zerone.backend.configuration.MailServiceConfig;
import ru.skillbox.zerone.backend.model.dto.request.MessageDTO;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MailService {
  private static final String ACCOUNT_CONFIRMATION_MESSAGE_THEME = "Подтверждение аккаунта";
  private static final String EMAIL_CONFIRMATION_MESSAGE_THEME = "Подтверждение смены пароля или Email";
  private final KafkaProducerMessage kafkaProducerMessage;
  private final MailServiceConfig mailServiceConfig;


  public void sendVerificationEmail(String email, String verifyCode, String pathUri) {
    MessageDTO messageDto = MessageDTO.builder()
        .email(email)
        .theme(ACCOUNT_CONFIRMATION_MESSAGE_THEME)
        .verificationLink(MessageFormatter.format("Пожалуйста, подтвердите ваш аккаунт, перейдя по ссылке: {}",
        createVerificationLink(email, verifyCode, pathUri, mailServiceConfig.getFrontAddress())).getMessage()).build();

    kafkaProducerMessage.sendMessage(messageDto);
  }

  public void sendVerificationChangeEmail(String email, String verifyCode, String pathUri) {
    MessageDTO messageDto = MessageDTO.builder()
        .email(email)
        .theme(EMAIL_CONFIRMATION_MESSAGE_THEME)
        .verificationLink(MessageFormatter.format("Пожалуйста, подтвердите смену email, перейдя по ссылке: {}",
        createVerificationLink(email, verifyCode, pathUri, mailServiceConfig.getServerAddress())).getMessage()).build();

    kafkaProducerMessage.sendMessage(messageDto);
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
