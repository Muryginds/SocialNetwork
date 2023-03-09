package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class VKCountryResponseDTO {
  private VKItemsResponseDTO response;

  @Data
  public static class VKItemsResponseDTO {
    private int count;
    private List<CountryDTO> items;

    @Data
    public static class CountryDTO {
      private short id;
      private String title;
    }
  }
}
