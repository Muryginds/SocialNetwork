package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JavaMailSender emailSender;
  @Value("${spring.mail.username}")
  private String senderMail;

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registerAccount(RegisterRequestDTO request) {

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new UserAlreadyExistException(request.getEmail());
    }

    var user = createUserFromRequest(request);
    var verificationUuid = UUID.randomUUID();
    user.setConfirmationCode(verificationUuid.toString());

    userRepository.save(user);

    var message = createVerificationMessage(user.getEmail(), verificationUuid.toString());
    emailSender.send(message);

    CommonResponseDTO<MessageResponseDTO> response = new CommonResponseDTO<>();
    response.setData(new MessageResponseDTO("ok"));
    return response;
  }

  public SimpleMailMessage createVerificationMessage(String mailToSend, String verifyCode) {
    var message = new SimpleMailMessage();
    message.setFrom(senderMail);
    message.setTo(mailToSend);
    message.setSubject("Verification message");
    message.setText(String.format("Verification code that you need to input: %s", verifyCode));
    return message;
  }

  public User createUserFromRequest(RegisterRequestDTO request) {
    return User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .lastOnlineTime(LocalDateTime.now())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
  }
}