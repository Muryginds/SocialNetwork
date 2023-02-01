package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

  private static final String VERIFICATION_MESSAGE_THEME = "Verification message";

  private final JavaMailSender emailSender;
  @Value("${spring.mail.username}")
  private String senderMail;

  public void sendVerificationEmail(String email, String verifyCode) {
    var message = createVerificationMessage(
        email,
        VERIFICATION_MESSAGE_THEME,
        String.format("Verification code that you need to input: %s", verifyCode)
    );
    emailSender.send(message);
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
