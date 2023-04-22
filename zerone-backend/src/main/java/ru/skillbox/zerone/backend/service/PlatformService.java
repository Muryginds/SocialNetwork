package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.BasicEntityDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.repository.CountryRepository;

import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlatformService {
  private final VKRestClientService vkRestClientService;
  private final CountryRepository countryRepository;

  private static final Map<String, BasicEntityDTO> LANGUAGES = Map.of(
      "Русский", new BasicEntityDTO(0L, "русский"),
      "English", new BasicEntityDTO(1L, "english"),
      "French", new BasicEntityDTO(2L, "french"),
      "Korean", new BasicEntityDTO(3L, "korean")
  );

  public CommonListResponseDTO<BasicEntityDTO> getLanguages() {
    return CommonListResponseDTO.<BasicEntityDTO>builder()
        .total(LANGUAGES.size())
        .perPage(LANGUAGES.size())
        .offset(0)
        .data(new ArrayList<>(LANGUAGES.values()))
        .build();
  }

  public CommonListResponseDTO<BasicEntityDTO> getCountries(String country, int offset, int itemPerPage) {
    var countriesPage = countryRepository.findAllByNameContains(country, PageRequest.of(offset, itemPerPage));
    var countriesDTO = countriesPage.map(c -> new BasicEntityDTO(c.getId().longValue(), c.getName())).toList();

    return CommonListResponseDTO.<BasicEntityDTO>builder()
        .total(countriesPage.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(countriesDTO)
        .build();
  }

  public CommonListResponseDTO<BasicEntityDTO> getCities(int countryId, String city, int offset, int itemPerPage) {
    var cities = vkRestClientService.getCities(countryId, city, offset, itemPerPage);

    return CommonListResponseDTO.<BasicEntityDTO>builder()
        .total(cities.size())
        .perPage(itemPerPage)
        .offset(offset)
        .data(cities)
        .build();
  }
}
