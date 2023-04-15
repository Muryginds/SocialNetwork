package ru.skillbox.zerone.backend.controller.swaggerdoc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.zerone.backend.model.dto.request.TagDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;

public interface SwaggerTagsController {
  @Operation(summary = "Добавление нового тега")
  @ApiResponse(responseCode = "200", description = "Тег успешно добавлен")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для добавления тега")
  CommonResponseDTO<TagDTO> addTag(@Valid @RequestBody TagDTO tagDTO);

  @Operation(summary = "Удаление тега по ID")
  @ApiResponse(responseCode = "200", description = "Тег успешно удален")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для удаления тега")
  CommonResponseDTO<MessageResponseDTO> deleteTag(@RequestParam Long id);

  @Operation(summary = "Получение списка тегов")
  @ApiResponse(responseCode = "200", description = "Список тегов успешно получен")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для получения списка тегов")
  CommonListResponseDTO<TagDTO> getAllTags(@RequestParam(value = "tag", defaultValue = "") String tag,
                                           @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                           @RequestParam(value = "itemPerPage", defaultValue = "10") @Min(0) @Max(100) Integer itemPerPage);
}
