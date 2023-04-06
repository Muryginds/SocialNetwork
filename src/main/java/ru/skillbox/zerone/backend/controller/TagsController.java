package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.TagDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.service.TagService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagsController {
  private final TagService tagService;

  @PostMapping
  public CommonResponseDTO<TagDTO> addTag(@Valid @RequestBody TagDTO tagDTO) {
    return tagService.addTag(tagDTO);
  }

  @DeleteMapping
  public CommonResponseDTO<MessageResponseDTO> deleteTag(@RequestParam(required = false) Long id) { return tagService.deleteTag(id);  }

  @GetMapping
  public CommonListResponseDTO<TagDTO> getAllTags(@RequestParam(value = "tag", defaultValue = "") String tag,
                                                  @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                                  @RequestParam(value = "itemPerPage", defaultValue = "10") @Min(0) @Max(100) Integer itemPerPage) {
    return tagService.getAllTags(tag, offset, itemPerPage);
  }
}
