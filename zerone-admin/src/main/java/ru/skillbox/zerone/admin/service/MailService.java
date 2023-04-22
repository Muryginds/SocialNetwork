package ru.skillbox.zerone.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender emailSender;
  @Value("${spring.mail.username}")
  private String senderMail;

  public void sendSimpleEmail(String toAddress, String subject, String message) {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setFrom(senderMail);
    simpleMailMessage.setTo(toAddress);
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setText(message);
    emailSender.send(simpleMailMessage);
  }
}
