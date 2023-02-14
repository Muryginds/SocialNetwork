package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MailService {

  private static final String CONFIRMATION_MESSAGE_THEME = "Account confirmation";

  private final JavaMailSender emailSender;
  @Value("${spring.mail.username}")
  private String senderMail;
  @Value("${mail-service.server-address}")
  private String serverAddress;

  public void sendVerificationEmail(String email, String verifyCode) {
    var message = createVerificationMessage(
        email,
        CONFIRMATION_MESSAGE_THEME,
        String.format("Please confirm your registration by clicking following link: %s", createVerificationLink(email, verifyCode))
    );
    emailSender.send(message);
  }

  private String createVerificationLink(String userId, String token) {
    return UriComponentsBuilder
        .fromHttpUrl(serverAddress)
        .path("/registration/complete")
        .queryParam("userId", URLEncoder.encode(userId, StandardCharsets.UTF_8))
        .queryParam("token", URLEncoder.encode(token, StandardCharsets.UTF_8))
        .build()
        .toUriString();
  }

  private SimpleMailMessage createVerificationMessage(String addressee, String theme, String text) {
    var message = new SimpleMailMessage();
    message.setFrom(senderMail);
    message.setTo(addressee);
    message.setSubject(theme);
    message.setText(text);
    return message;
  }
}