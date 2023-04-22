package ru.skillbox.zerone.admin.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.admin.model.dto.ChoiceDto;
import ru.skillbox.zerone.admin.model.dto.ChoiceListDto;
import ru.skillbox.zerone.admin.model.entity.SupportRequest;
import ru.skillbox.zerone.admin.model.enumerated.SupportRequestStatus;
import ru.skillbox.zerone.admin.repository.SupportRequestRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChoiceService {

  private final SupportRequestRepository supportRequestRepository;
  private final MailService mailService;

  public ChoiceListDto getChoiceListDto() {
    List<ChoiceDto> choiceDtos = getNewRequests();
    return ChoiceListDto.builder()
        .choiceDtos(choiceDtos)
        .build();
  }

  @Transactional
  public ChoiceDto getRequestById(Long id) {
    SupportRequest requestEntity = supportRequestRepository.findById(id)
        .orElseThrow(EntityNotFoundException::new);
    requestEntity.setStatus(SupportRequestStatus.IN_WORK);
    supportRequestRepository.save(requestEntity);
    return convertRequestEntityToChoiceDto(requestEntity);
  }

  public void processAnswer(ChoiceDto choiceDto) {
    SupportRequest supportRequest = supportRequestRepository
        .findById(choiceDto.getId())
        .orElseThrow(EntityNotFoundException::new);
    supportRequest.setStatus(SupportRequestStatus.CLOSED);
    supportRequest.setAnswer(choiceDto.getAnswer());
    supportRequestRepository.save(supportRequest);
    sendEmail(supportRequest);
  }

  private List<ChoiceDto> getNewRequests() {
    List<SupportRequest> requests =
        supportRequestRepository.findAllByStatusOrderById(SupportRequestStatus.NEW);
    List<ChoiceDto> choiceDtos = new ArrayList<>();
    requests.forEach(request ->
      choiceDtos.add(convertRequestEntityToChoiceDto(request))
    );
    return choiceDtos;
  }

  private ChoiceDto convertRequestEntityToChoiceDto(SupportRequest request) {
    return ChoiceDto.builder()
        .id(request.getId())
        .email(request.getEmail())
        .fullName(request.getFirstName() + ' ' + request.getLastName())
        .message(request.getMessage())
        .createdAt(request.getTime()
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss")))
        .build();
  }

  private void sendEmail(SupportRequest supportRequest) {
    String message = supportRequest.getFirstName() + " " +
        supportRequest.getLastName() + "!\n\n";
    message += supportRequest.getAnswer() + "\n\n" +
        "С уважением,  Служба поддержки Zerone.\n";

    mailService.sendSimpleEmail(
        supportRequest.getEmail(),
        "Zerone Support Service",
        message);
  }

  public void sendEmail(ChoiceDto dto) {
    String message = dto.getFullName() + "!\n\n" +
        "Ваша заявка от " + dto.getCreatedAt() + " принята в обработку.\n\n" +
        "С уважением, Служба поддержки Zerone.\n";
    mailService.sendSimpleEmail(
        dto.getEmail(),
        "Zerone Support Service",
        message);
  }
}
