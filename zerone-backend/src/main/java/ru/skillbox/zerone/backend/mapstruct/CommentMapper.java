package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.entity.Comment;

import java.util.List;

@Mapper
@DecoratedWith(CommentMapperDecorator.class)
public interface CommentMapper {
  @Mapping(target = "parentId", source = "comment.parent.id")
  @Mapping(target = "post", source = "comment.post.id")
  @Mapping(target = "blocked", source = "comment.isBlocked")
  @Mapping(target = "deleted", source = "comment.isDeleted")
  @Mapping(target = "subComments", expression = "java(commentListToCommentDTOList(comment.getComments()))")
  CommentDTO commentToCommentDTO(Comment comment);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isBlocked", ignore = true)
  @Mapping(target = "comments", ignore = true)
  @Mapping(target = "isDeleted", ignore = true)
  @Mapping(target = "author", expression = "java(ru.skillbox.zerone.backend.util.CurrentUserUtils.getCurrentUser())")
  @Mapping(target = "type", expression = "java(dto.getParentId() == null ? ru.skillbox.zerone.backend.model.enumerated.CommentType.POST : ru.skillbox.zerone.backend.model.enumerated.CommentType.COMMENT)")
  Comment dtoToComment(CommentRequestDTO dto, long postId);


  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isBlocked", ignore = true)
  @Mapping(target = "comments", ignore = true)
  @Mapping(target = "time", ignore = true)
  @Mapping(target = "isDeleted", ignore = true)
  @Mapping(target = "parent", ignore = true)
  @Mapping(target = "author", expression = "java(ru.skillbox.zerone.backend.util.CurrentUserUtils.getCurrentUser())")
  @Mapping(target = "type", expression = "java(dto.getParentId() == null ? ru.skillbox.zerone.backend.model.enumerated.CommentType.POST : ru.skillbox.zerone.backend.model.enumerated.CommentType.COMMENT)")
  void updateComment(@MappingTarget Comment comment, CommentRequestDTO dto);

  List<CommentDTO> commentListToCommentDTOList(List<Comment> comment);

}
