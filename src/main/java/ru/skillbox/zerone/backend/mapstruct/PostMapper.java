package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.Tag;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Mapper
@DecoratedWith(PostMapperDecorator.class)
public interface PostMapper {
  PostDTO postToPostsDTO(Post post);

  String tagToStringTag(Tag tag);

  default Long convertLocalDateTimeToLong(LocalDateTime time) {
    return time.toInstant(ZoneOffset.of("+03:00")).getEpochSecond();
  }
}
