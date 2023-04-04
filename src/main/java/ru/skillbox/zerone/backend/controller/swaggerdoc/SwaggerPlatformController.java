package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.skillbox.zerone.backend.model.dto.response.BasicEntityDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;

@Tag(name = "Контроллер для загрузки списка стран, языков и городов")
public interface SwaggerPlatformController {

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список языков сформирован"))
  @Operation(summary = "Получение списка языков")
  CommonListResponseDTO<BasicEntityDTO> getLanguages();

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список городов сформирован"))
  @Operation(summary = "Получение списка городов")
  CommonListResponseDTO<BasicEntityDTO> getCities(int countryId, String city, int offset, int itemPerPage);

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список стран сформирован"))
  @Operation(summary = "Получение списка стран")
  CommonListResponseDTO<BasicEntityDTO> getCountries(String country, int offset, int itemPerPage);
}
