package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.controller.ChangePasswordTokenController;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.ChangePasswordTokenDto;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {
  private final UserRepository userRepository;
  private final MailService mailService;
  private final UserMapper userMapper;

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> changePassword(ChangePasswordTokenDto request) {

    User user = userMapper.registerRequestDTOToUser(request);
     CurrentUserUtils.getCurrentUser().setPassword(request.getPassword());



    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();

  }
}
