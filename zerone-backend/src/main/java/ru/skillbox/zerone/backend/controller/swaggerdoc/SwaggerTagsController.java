package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.zerone.backend.model.dto.request.TagDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;

@Tag(name = "Контроллер для управления тэгами")
public interface SwaggerTagsController {

  @Operation(summary = "Добавить новый тэг")
  @ApiResponse(responseCode = "200", description = "Тэг успешно добавлен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponseDTO.class)))
  @ApiResponse(responseCode = "400", description = "Некорректный запрос. Проверьте параметры запроса.", content = @Content)
  CommonResponseDTO<TagDTO> addTag(@Valid @RequestBody TagDTO tagDTO);

  @Operation(summary = "Удалить тэг")
  @ApiResponse(responseCode = "200", description = "Тэг успешно удален", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponseDTO.class)))
  @ApiResponse(responseCode = "400", description = "Некорректный запрос. Проверьте параметры запроса.", content = @Content)
  CommonResponseDTO<MessageResponseDTO> deleteTag();

  @Operation(summary = "Получить список всех тэгов")
  @ApiResponse(responseCode = "200", description = "Список тэгов успешно получен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonListResponseDTO.class)))
  @ApiResponse(responseCode = "400", description = "Некорректный запрос. Проверьте параметры запроса.", content = @Content)
  CommonListResponseDTO<TagDTO> getAllTags(
      @RequestParam(value = "tag", defaultValue = "") String tag,
      @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(value = "itemPerPage", defaultValue = "10") @Min(0) @Max(100) Integer itemPerPage
  );
}