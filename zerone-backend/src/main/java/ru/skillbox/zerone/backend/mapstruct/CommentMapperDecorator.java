package ru.skillbox.zerone.backend.mapstruct;

import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.repository.CommentRepository;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.List;

public abstract class CommentMapperDecorator implements CommentMapper {

  @Autowired
  private CommentMapper mapper;

  @Autowired
  private LikeRepository likeRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Override
  public CommentDTO commentToCommentDTO(Comment comment) {
    CommentDTO commentDTO = mapper.commentToCommentDTO(comment);
    commentDTO.setLikes(likeRepository.countByComment(comment));
    commentDTO.setMyLike(likeRepository.findByUserAndComment(CurrentUserUtils.getCurrentUser(), comment).isPresent());
    return commentDTO;
  }

  @Override
  public Comment dtoToComment(CommentRequestDTO dto, long postId) {
    Comment comment = mapper.dtoToComment(dto, postId);
    Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
    comment.setPost(post);
    if (dto.getParentId() != null) {
      Comment parentComment = commentRepository.findById(dto.getParentId()).orElseThrow(() -> new CommentNotFoundException(String.format("Комментарий с id %d не найден", dto.getParentId())));
      comment.setParent(parentComment);
    }
    return comment;
  }

  @Override
  public List<CommentDTO> commentListToCommentDTOList(List<Comment> comment) {
    return comment.stream().map(this::commentToCommentDTO).toList();
  }
}
