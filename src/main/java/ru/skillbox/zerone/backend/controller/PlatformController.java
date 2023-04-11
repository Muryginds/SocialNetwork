package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zerone.backend.controller.swaggerdoc.SwaggerPlatformController;
import ru.skillbox.zerone.backend.model.dto.response.BasicEntityDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.service.PlatformService;

@RestController
@RequestMapping(value = "/api/v1/platform", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PlatformController implements SwaggerPlatformController {
  private final PlatformService platformService;

  @Override
  @GetMapping("/languages")
  public CommonListResponseDTO<BasicEntityDTO> getLanguages() {
    return platformService.getLanguages();
  }

  @Override
  @GetMapping("/cities")
  public CommonListResponseDTO<BasicEntityDTO> getCities(@RequestParam("countryId") int countryId,
                                                         @RequestParam(name = "city", defaultValue = "") String city,
                                                         @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                         @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage) {
    return platformService.getCities(countryId, city, offset, itemPerPage);
  }

  @Override
  @GetMapping("/countries")
  public CommonListResponseDTO<BasicEntityDTO> getCountries(@RequestParam(name = "country", defaultValue = "") String country,
                                                            @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                            @RequestParam(name = "itemPerPage", defaultValue = "250") int itemPerPage) {
    return platformService.getCountries(country, offset, itemPerPage);
  }
}
