package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class VKCityResponseDTO {
  private VKItemsResponseDTO response;

  @Data
  public static class VKItemsResponseDTO {
    private int count;
    private List<CityDTO> items;

    @Data
    public static class CityDTO {
      private long id;
      private String title;
      private String region;
      private String area;
    }
  }
}
