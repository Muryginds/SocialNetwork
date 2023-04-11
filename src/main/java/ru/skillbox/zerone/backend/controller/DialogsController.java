package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.controller.swaggerdoc.SwaggerDialogsController;
import ru.skillbox.zerone.backend.model.dto.request.DialogRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.MessageRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.service.DialogService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/dialogs", produces = MediaType.APPLICATION_JSON_VALUE)
public class DialogsController implements SwaggerDialogsController {
  private final DialogService dialogService;

  @Override
  @GetMapping("/unreaded")
  public CommonResponseDTO<CountDTO> getUnreadedMessages() {
    return dialogService.getUnreaded();
  }

  @Override
  @PostMapping("/{id}/messages")
  public CommonResponseDTO<MessageDataDTO> postMessages(@PathVariable long id,
                                                        @RequestBody MessageRequestDTO messageRequestDTO) {
    return dialogService.postMessages(id, messageRequestDTO);
  }

  @Override
  @GetMapping("/{id}/messages")
  public CommonListResponseDTO<MessageDataDTO> getMessages(@PathVariable long id,
                                                           @RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                           @RequestParam(name = "itemPerPage", defaultValue = "1000") @Min(0) int itemPerPage) {
    return dialogService.getMessages(id, offset, itemPerPage);
  }

  @Override
  @PostMapping
  public CommonResponseDTO<DialogDataDTO> postDialogs(@Valid @RequestBody DialogRequestDTO dialogRequestDTO) {
    return dialogService.postDialogs(dialogRequestDTO);
  }

  @Override
  @GetMapping
  public CommonListResponseDTO<DialogDataDTO> getDialogs(@RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                         @RequestParam(name = "itemPerPage", defaultValue = "1000") @Min(0) int itemPerPage) {
    return dialogService.getDialogs(offset, itemPerPage);
  }
}
