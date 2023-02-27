package ru.skillbox.zerone.backend.service;

import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.LanguageDTO;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PlatformService {
  private static final Map<String, LanguageDTO> map = Map.of(
      "Русский", new LanguageDTO(0L, "русский"),
      "English", new LanguageDTO(1L, "english"),
      "French", new LanguageDTO(2L, "french"),
      "Korea", new LanguageDTO(3L, "korea")
  );
  public CommonListResponseDTO<LanguageDTO> getLanguages() {
    return CommonListResponseDTO.<LanguageDTO>builder()
        .total(map.size())
        .perPage(map.size())
        .offset(0)
        .data(new ArrayList<>(map.values()))
        .error("error")
        .timestamp(LocalDateTime.now())
        .build();
  }}
