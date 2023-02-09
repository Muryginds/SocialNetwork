package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.request.SupportRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.entity.SupportRequest;
import ru.skillbox.zerone.backend.repository.SupportRequestRepository;

@Service
@RequiredArgsConstructor
public class SupportRequestService {
  private final SupportRequestRepository supportRequestRepository;

  public CommonResponseDTO<MessageResponseDTO> registerSupportRequest(SupportRequestDTO requestDTO) {
    var supportRequest = SupportRequest.builder()
        .firstName(requestDTO.getFirstName())
        .lastName(requestDTO.getLastName())
        .email(requestDTO.getEmail())
        .message(requestDTO.getMessage())
        .build();

    supportRequestRepository.save(supportRequest);

    return CommonResponseDTO.<MessageResponseDTO>builder().data(new MessageResponseDTO("Ok")).build();
  }
}
