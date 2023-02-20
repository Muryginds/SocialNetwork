package ru.skillbox.zerone.backend.service;

import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.LanguageDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class PlatformService {
  private static final Map<String, LanguageDTO> map = new HashMap<>();
  public PlatformService(){
    map.put("Русский", new LanguageDTO(0L, "Русский"));
    map.put("English", new LanguageDTO(1L, "English"));
    map.put("French", new LanguageDTO(2L, "French"));
  }
  public CommonListResponseDTO<LanguageDTO> getLanguages() {
    return CommonListResponseDTO.<LanguageDTO>builder()
        .total(3)
        .perPage(2)
        .offset(0)
        .data(new ArrayList<>(map.values()))
        .error("error")
        .timestamp(LocalDateTime.now())
        .build();
  }
  }

