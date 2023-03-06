package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zerone.backend.model.dto.response.BasicEntityDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.service.PlatformService;

@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
public class PlatformController {
  private final PlatformService platformService;

  @GetMapping("/languages")
  public CommonListResponseDTO<BasicEntityDTO> getLanguages() {
    return platformService.getLanguages();
  }

  @GetMapping("/cities")
  public CommonListResponseDTO<BasicEntityDTO> getCities(@RequestParam("countryId") int countryId,
                                                         @RequestParam(name = "city", defaultValue = "") String city,
                                                         @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                         @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage) {
    return platformService.getCities(countryId, city, offset, itemPerPage);
  }

  @GetMapping("/countries")
  public CommonListResponseDTO<BasicEntityDTO> getCountries(@RequestParam(name = "country", defaultValue = "") String country,
                                                            @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                            @RequestParam(name = "itemPerPage", defaultValue = "250") int itemPerPage) {
    return platformService.getCountries(country, offset, itemPerPage);
  }
}
