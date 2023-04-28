package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
  @Mapping(target = "parentId", source = "comment.parent.id")
  @Mapping(target = "post", source = "comment.post.id")
  @Mapping(target = "subComments", expression = "java(commentListToCommentDTOList(comment.getComments()))")
  CommentDTO commentToCommentDTO(Comment comment);

  List<CommentDTO> commentListToCommentDTOList(List<Comment> comment);

}
