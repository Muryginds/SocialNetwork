package ru.skillbox.zerone.admin.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import ru.skillbox.zerone.admin.model.dto.ChoiceDto;

@Tag(name = "Контроллер для работы с Службой поддержки")
public interface SwaggerSupportController {
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Возвращает форму выбора обращения клиента")
  })
  @Operation(summary = "Ответ на вызов Службы поддержки")
  String doChoice(Model model);

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Возвращает форму ответа клиенту")
  })
  @Operation(summary = "Подготовка ответа на обращение клиента")
  String doAnswer(@PathVariable Long id, Model model);

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Возвращает форму выбора обращения клиента")
  })
  @Operation(summary = "Отправка ответа клиенту")
  String answer(@PathVariable Long id, ChoiceDto choiceDto);
}
