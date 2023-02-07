package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.request.SupportRequestDTO;
import ru.skillbox.zerone.backend.model.entity.SupportRequest;
import ru.skillbox.zerone.backend.repository.SupportRequestRepository;

@Service
@RequiredArgsConstructor
public class SupportRequestService {
  private final SupportRequestRepository supportRequestRepository;

  public ResponseEntity<Object> registerSupportRequest(SupportRequestDTO requestDTO) {
    var supportRequest = SupportRequest.builder()
        .firstName(requestDTO.getFirstName())
        .lastName(requestDTO.getLastName())
        .email(requestDTO.getEmail())
        .message(requestDTO.getMessage())
        .build();

    supportRequestRepository.saveAndFlush(supportRequest);

    return ResponseEntity.ok().build();
  }
}
