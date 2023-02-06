package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private static final String CONFIRMATION_MESSAGE_THEME = "Account confirmation";

  private final JavaMailSender emailSender;
  @Value("${spring.mail.username}")
  private String senderMail;
  @Value("${mail_service.server_address}")
  private String serverAddress;

  public void sendVerificationEmail(String email, String verifyCode) {
    var message = createVerificationMessage(
        email,
        CONFIRMATION_MESSAGE_THEME,
        String.format("Please confirm your registration by clicking following link: %s", createVerificationLink(email, verifyCode))
    );
    emailSender.send(message);
    log.info("IN sendVerificationEmail - user with username: {} mail sent successfully", email);
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
