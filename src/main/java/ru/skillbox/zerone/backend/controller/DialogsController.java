package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.DialogRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.MessageRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.service.DialogsService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dialogs")
public class DialogsController {
  private final DialogsService dialogsService;

  @GetMapping("/unreaded")
  public CommonResponseDTO<CountDTO> getUnreadedMessages() {
    return dialogsService.getUnreaded();
  }

  @PostMapping("/{id}/messages")
  public CommonResponseDTO<MessageDataDTO> postMessages(@PathVariable @Min(1) Long id,
                                                       @RequestBody MessageRequestDTO messageRequestDTO) {
    return dialogsService.postMessages(id, messageRequestDTO);
  }

  @GetMapping("/{id}/messages")
  public CommonListResponseDTO<MessageDataDTO> getMessages(@PathVariable @Min(1) Long id,
                                                           @RequestParam(name = "query", defaultValue = "") String query,
                                                           @RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                           @RequestParam(name = "itemPerPage", defaultValue = "1000") @Min(0) int itemPerPage,
                                                           @RequestParam(name = "fromMessageId", defaultValue = "0") @Min(0) int fromMessageId) {
    return dialogsService.getMessages(id, query, offset, itemPerPage, fromMessageId);
  }

  @PostMapping
  public CommonResponseDTO<DialogDataDTO> postDialogs(@Valid @RequestBody DialogRequestDTO dialogRequestDTO) {
    return dialogsService.postDialogs(dialogRequestDTO);
  }

  @GetMapping
  public CommonListResponseDTO<DialogDataDTO> getDialogs(@RequestParam(name = "name", defaultValue = "") String name,
                                                                        @RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                                        @RequestParam(name = "itemPerPage", defaultValue = "1000") @Min(0) int itemPerPage) {
    return dialogsService.getDialogs(name, offset, itemPerPage);
  }
}
