package ru.skillbox.zerone.backend.service;

import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.BasicEntityDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;

import java.util.ArrayList;
import java.util.Map;

@Service
public class PlatformService {
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
    //
    return CommonListResponseDTO.<BasicEntityDTO>builder()
        //.total(LANGUAGES.size())
        .perPage(itemPerPage)
        .offset(offset)
        //.data(new ArrayList<>(LANGUAGES.values()))
        .build();
  }

  public CommonListResponseDTO<BasicEntityDTO> getCities(int countryId, String city, int offset, int itemPerPage) {
    //
    return CommonListResponseDTO.<BasicEntityDTO>builder()
        //.total(LANGUAGES.size())
        .perPage(itemPerPage)
        .offset(offset)
        //.data(new ArrayList<>(LANGUAGES.values()))
        .build();
  }
}
