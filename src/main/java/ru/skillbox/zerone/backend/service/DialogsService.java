package ru.skillbox.zerone.backend.service;

import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.request.DialogRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.MessageRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.DialogDataDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageDataDTO;

@Service
public class DialogsService {

  public CommonListResponseDTO<MessageDataDTO> getMessages(Long id, String query, int offset, int itemPerPage, int fromMessageId) {
    return null;
  }

  public CommonResponseDTO<MessageDataDTO> postMessages(Long id, MessageRequestDTO messageRequestDTO) {
    return null;
  }

  public CommonResponseDTO<Object> getUnreaded() {
    return null;
  }

  public CommonResponseDTO<DialogDataDTO> postDialogs(DialogRequestDTO dialogRequestDTO) {
    return null;
  }

  public CommonListResponseDTO<DialogDataDTO> getDialogs(String name, int offset, int itemPerPage) {
    return null;
  }
}
