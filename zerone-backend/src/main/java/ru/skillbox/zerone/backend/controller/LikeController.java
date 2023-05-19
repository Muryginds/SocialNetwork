package ru.skillbox.zerone.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.LikeRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.LikeData;
import ru.skillbox.zerone.backend.model.dto.response.LikesCountResponse;
import ru.skillbox.zerone.backend.model.enumerated.LikeType;
import ru.skillbox.zerone.backend.service.LikeService;

import java.beans.PropertyEditorSupport;


@RestController
@Tag(name = "Контроллер для работы с лайками")
@AllArgsConstructor
@RequestMapping("/api/v1/")
public class LikeController {

  private final LikeService likeService;

  @InitBinder
  public void initBinder(final WebDataBinder webDataBinder) {
    webDataBinder.registerCustomEditor(LikeType.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String name) throws IllegalArgumentException {
        setValue(LikeType.findByName(name));
      }
    });
  }

  @Operation(summary = "Поставить лайк")
  @PutMapping("/likes")
  public CommonResponseDTO<LikeData> putLike(@RequestBody LikeRequestDTO likeRequestDTO) {
    return likeService.putLike(likeRequestDTO);
  }

  @Operation(summary = "Удалить лайк")
  @DeleteMapping("/likes")
  public LikesCountResponse deleteLikes(@RequestParam(name = "item_id") Long id,
                                        @RequestParam(name = "type") LikeType type) {
    return likeService.deleteLike(id, type);
  }

  @Operation(summary = "Получить лайки")
  @GetMapping("/likes")
  public CommonResponseDTO<LikeData> getLikes(@RequestParam(name = "item_id") Long id,
                                              @RequestParam(name = "type") LikeType type) {
    return likeService.getLikes(id, type);
  }
}

