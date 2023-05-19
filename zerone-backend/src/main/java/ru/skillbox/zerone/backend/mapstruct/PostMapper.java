package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.entity.Post;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@Mapper
@DecoratedWith(PostMapperDecorator.class)
public interface PostMapper {

  @Mapping(target = "tags", expression = "java(post.getTags() == null ? java.util.Collections.emptyList() : post.getTags().stream().map(ru.skillbox.zerone.backend.model.entity.Tag::getName).toList())")
  PostDTO postToPostsDTO(Post post);

  List<PostDTO> toDtoList(List<Post> postList);

  default Long convertLocalDateTimeToLong(LocalDateTime time) {

    return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
  }
}
