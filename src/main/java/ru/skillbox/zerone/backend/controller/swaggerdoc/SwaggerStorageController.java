package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;

@Tag(name = "Контроллер для хранения файлов")
@ApiResponse(responseCode = "403", description = "Пользователь не авторизован", content = @Content)
public interface SwaggerStorageController {

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Файл успешно загружен"),
      @ApiResponse(responseCode = "400", description = "Ошибка при загрузке файла",
          content = @Content)})
  @Operation(summary = "Загрузка изображения")
  CommonResponseDTO<StorageDTO> postImage(@NotNull MultipartFile file);

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Файл успешно удален"),
      @ApiResponse(responseCode = "400", description = "Ошибка при удалении файла",
          content = @Content)})
  @Operation(summary = "Удаление изображения по его идентификатору")
  CommonResponseDTO<String> deleteImage(@PathVariable("public_id") String publicId);
}
