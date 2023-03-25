package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.entity.Post;

@Mapper(uses = {UserMapper.class, TagMapper.class})
public interface PostMapper {
  PostDTO postToPostsDTO (Post post);
}
