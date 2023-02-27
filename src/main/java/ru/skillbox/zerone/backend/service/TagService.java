package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.CommonListDTO;
import ru.skillbox.zerone.backend.model.dto.response.TagDTO;
import ru.skillbox.zerone.backend.model.entity.Tag;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.repository.TagRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

@Service
@AllArgsConstructor
public class TagService {

  private final TagRepository tagRepository;
  private final PostRepository postRepository;

//  public CommonListDTO<TagDTO> getTags (String tag, int offset, int itemPerPage) {
//    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
//    Page<Tag> pageableTagList = tagRepository.findTagsContains(tag, pageable);
//    List<TagDTO> result = pageableTagList.stream().map(x -> new TagDTO().setId(x.getId()).setTag(x.getTag())).collect(Collectors.toList());
//    CommonListDTO<TagDTO> response = new CommonListDTO<>();
//    response.setPerPage(itemPerPage);
//    response.setTimestamp(LocalDateTime.now());
//    response.setOffset(offset);
//    response.setTotal((int) pageableTagList.getTotalElements());
//    response.setData(result);
//    return response;
//  }
}
