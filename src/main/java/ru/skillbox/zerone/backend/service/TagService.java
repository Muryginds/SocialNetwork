package ru.skillbox.zerone.backend.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.TagNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.TagMapper;
import ru.skillbox.zerone.backend.model.dto.request.TagDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.entity.Tag;
import ru.skillbox.zerone.backend.repository.TagRepository;
import ru.skillbox.zerone.backend.util.ResponseUtils;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;
  private final TagMapper tagMapper;

  @Transactional
  public CommonResponseDTO<TagDTO> addTag(TagDTO tagDTO) {

    var getTag = tagDTO.getTag();
    if ((getTag==null)||(getTag.isBlank())) {
      throw new TagNotFoundException("Тэг не может быть пустым");
    }

      String tagName = tagDTO.getTag();
      Optional<Tag> optTag = tagRepository.findByName(tagName);
        TagDTO resultTagDTO;

       if (optTag.isEmpty()) {
         Tag tag = tagMapper.tagDTOToTag(tagDTO);
         tagRepository.save(tag);
         resultTagDTO = tagMapper.tagToTagDTO(tag);
          }
       else {
         resultTagDTO = tagMapper.tagToTagDTO(optTag.get());
       }

    return CommonResponseDTO.<TagDTO>builder()
        .data(resultTagDTO)
        .build();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> deleteTag (Long id) {

    tagRepository.deleteById(id);

    return ResponseUtils.commonResponseDataOk();
  }

  @Transactional
  public CommonListResponseDTO<TagDTO> getAllTags(String tagName, Integer offset, Integer itemPerPage) {
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Tag> pageableTagList;

    if (tagName.isEmpty()) {
      pageableTagList = tagRepository.findAll(pageable);
    } else {
      pageableTagList = tagRepository.findByNameContains(tagName, pageable);
    }

    List<TagDTO> tagDTOList = pageableTagList.map(tagMapper::tagToTagDTO).toList();


    return CommonListResponseDTO.<TagDTO>builder()
        .total(pageableTagList.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(tagDTOList)
        .build();

  }
}
