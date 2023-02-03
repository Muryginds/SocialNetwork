package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.model.UserDto;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.security.jwt.JwtUser;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final MailService mailService;


  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registerAccount(RegisterRequestDTO request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistException(request.getEmail());
    }

    User user = createUserFromRequest(request);
    var verificationUuid = UUID.randomUUID();
    user.setConfirmationCode(verificationUuid.toString());

    userRepository.save(user);

    mailService.sendVerificationEmail(user.getEmail(), verificationUuid.toString());

    CommonResponseDTO<MessageResponseDTO> response = new CommonResponseDTO<>();
    response.setData(new MessageResponseDTO("ok"));
    return response;
  }

  private User createUserFromRequest(RegisterRequestDTO request) {
    return User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .lastOnlineTime(LocalDateTime.now())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registrationComplete(String confirmationKey, String email) {
    var userOptional = userRepository.findUserByEmail(email);
    if (userOptional.isEmpty()) {
      throw new RegistrationCompleteException("wrong input");
    }
    User user = userOptional.get();
    CommonResponseDTO<MessageResponseDTO> response = new CommonResponseDTO<>();

    if (!user.getConfirmationCode().equals(confirmationKey)) {
      throw new RegistrationCompleteException("wrong confirmation key");
    }

    user.setIsApproved(true);
    userRepository.save(user);

    response.setData(new MessageResponseDTO("ok"));

    return response;
  }

  public CommonResponseDTO<UserDto> getCurrentUser() {

    try {
      JwtUser currentUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      User user = findByEmail(currentUser.getEmail());


      CommonResponseDTO<UserDto> responseDto = new CommonResponseDTO<>();
      responseDto.setData(fromUser(user));
      return responseDto;

    } catch (ClassCastException e) {

        throw new BadCredentialsException("Invalid username or password");
    }
  }

  public UserDto fromUser(User user) {
    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setFirstName(user.getFirstName());
    userDto.setLastName(user.getLastName());
    userDto.setEmail(user.getEmail());
    userDto.setCountry(user.getCountry());
    userDto.setCity(user.getCity());
    userDto.setBirthDate(user.getBirthDate());
    userDto.setRegDate(user.getRegDate());
    userDto.setPhoto(user.getPhoto());
    userDto.setAbout(user.getAbout());
    userDto.setBlocked(user.getIsBlocked());
    userDto.setDeleted(user.getIsDelete());
    userDto.setMessagesPermission(user.getMessagePermissions());
    userDto.setLastOnlineTime(user.getLastOnlineTime());
    userDto.setPhone(user.getPhone());

    return userDto;
  }

  public User findByEmail(String email) {
    return userRepository.findUserByEmail(email).orElse(null);
  }
}