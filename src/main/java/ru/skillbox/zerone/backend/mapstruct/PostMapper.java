package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import ru.skillbox.zerone.backend.model.dto.response.PostsDTO;
import ru.skillbox.zerone.backend.model.entity.Post;


@Mapper
public interface PostMapper {
  PostsDTO postToPostsDTO (Post post);
}
