package ru.skillbox.zerone.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.LikeRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.LikeData;
import ru.skillbox.zerone.backend.model.dto.response.LikesCountResponse;
import ru.skillbox.zerone.backend.service.LikeService;


@RestController
@Tag(name = "Контроллер для работы с лайками")
@AllArgsConstructor
@RequestMapping("/api/v1/")
public class LikeController {

  private final LikeService likeService;

  @Operation(summary = "Поставить лайк")
  @PutMapping("/likes")
  public CommonResponseDTO<LikeData> putLikes(@RequestBody LikeRequestDTO likeRequestDTO) {
    return likeService.putLikes(likeRequestDTO);
  }

  @Operation(summary = "Удалить лайк")
  @DeleteMapping("/likes")
  public LikesCountResponse deleteLikes(@RequestParam(name = "item_id") Long id,
                                        @RequestParam(name = "type") String type) {
    return likeService.deleteLike(id, type);
  }

  @Operation(summary = "Получить лайки")
  @GetMapping("/likes")
  public CommonResponseDTO<LikeData> getLikes(@RequestParam(name = "item_id") Long id,
                                              @RequestParam(name = "type") String type) {
    return likeService.getLikes(id, type);
  }
}

