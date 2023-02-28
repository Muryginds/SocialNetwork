package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.mapstruct.SupportRequestMapper;
import ru.skillbox.zerone.backend.model.dto.request.SupportRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.repository.SupportRequestRepository;

@Service
@RequiredArgsConstructor
public class SupportRequestService {
  private final SupportRequestRepository supportRequestRepository;
  private final SupportRequestMapper supportRequestMapper;

  public CommonResponseDTO<MessageResponseDTO> registerSupportRequest(SupportRequestDTO requestDto) {
    var supportRequest = supportRequestMapper.supportRequestDtoToSupportRequest(requestDto);

    supportRequestRepository.save(supportRequest);

    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();
  }
}
