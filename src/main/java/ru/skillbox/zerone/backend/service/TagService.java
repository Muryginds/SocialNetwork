package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.TagDTO;
import ru.skillbox.zerone.backend.model.entity.Tag;
import ru.skillbox.zerone.backend.repository.TagRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TagService {

  private final TagRepository tagRepository;

  public CommonListResponseDTO<TagDTO> getTags (String tag, int offset, int itemPerPage) {
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Tag> pageableTagList = tagRepository.findByTagContains(tag, pageable);
    List<TagDTO> result = new ArrayList<>();

    return CommonListResponseDTO.<TagDTO>builder()
        .total((int)pageableTagList.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .timestamp(LocalDateTime.now())
        .data(result)
        .build();

  }

  public CommonResponseDTO<TagDTO> postTag(TagDTO tag) {
    CommonResponseDTO<TagDTO> response = new CommonResponseDTO<>();


    return response;
  }


//  public CommonResponseDTO<SuccessResponse> deleteTag(int id) {
//    Tag tag = tagRepository.findById(id)
//        .orElseThrow(() -> new EntityNotFoundException("Tag is not exist"));
//    Set<Post> postsWithTag = postRepository.findPostsByTag(tag.getTag());
//    String response = "ok";
//    if (postsWithTag.isEmpty()) {
//      tagRepository.deleteById(id);
//    } else {
//      response = "tag use in another posts";
//    }
//    return new CommonResponseDTO<SuccessResponse>().setTimestamp(LocalDateTime.now()).setData(new SuccessResponse().setMessage(response));
//  }
}