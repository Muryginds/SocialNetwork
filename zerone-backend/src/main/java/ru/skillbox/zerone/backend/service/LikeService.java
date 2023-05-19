package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.ZeroneException;
import ru.skillbox.zerone.backend.model.dto.request.LikeRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.LikeData;
import ru.skillbox.zerone.backend.model.dto.response.LikesCountResponse;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.LikeType;
import ru.skillbox.zerone.backend.repository.CommentRepository;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class LikeService {
  private final LikeRepository likeRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;


  public CommonResponseDTO<LikeData> putLike(LikeRequestDTO likeRequestDTO) {
    User user = CurrentUserUtils.getCurrentUser();
    Like.LikeBuilder likeBuilder = Like.builder().type(likeRequestDTO.getType()).user(user);
    switch (likeRequestDTO.getType()) {
      case COMMENT -> {
        Comment comment = commentRepository.findById(likeRequestDTO.getId()).orElseThrow(() -> new CommentNotFoundException(String.format("Комментарий с id %d не найден", likeRequestDTO.getId())));
        if (likeRepository.findByUserAndComment(user, comment).isEmpty()) {
          likeRepository.save(likeBuilder.comment(comment).build());
        }
      }
      case POST -> {
        Post post = postRepository.findById(likeRequestDTO.getId()).orElseThrow(() -> new PostNotFoundException(likeRequestDTO.getId()));
        if (likeRepository.findByUserAndPost(user, post).isEmpty()) {
          likeRepository.save(likeBuilder.post(post).build());
        }
      }
      default -> throw new ZeroneException("Не задан корректный тип лайка");
    }
    return getLikesResponse(likeRequestDTO.getId(), likeRequestDTO.getType());
  }

  public CommonResponseDTO<LikeData> getLikes(Long id, LikeType type) {
    return getLikesResponse(id, type);
  }

  public LikesCountResponse deleteLike(Long id, LikeType type) {
    User user = CurrentUserUtils.getCurrentUser();
    Integer likeCount;

    switch (type) {
      case POST -> {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        likeRepository.findByUserAndPost(user, post).ifPresent(likeRepository::delete);
        likeCount = likeRepository.countByPost(post);
      }
      case COMMENT -> {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
        likeRepository.findByUserAndComment(user, comment).ifPresent(likeRepository::delete);
        likeCount = likeRepository.countByComment(comment);
      }
      default -> throw new ZeroneException("Не задан корректный тип лайка");
    }

    return LikesCountResponse.builder()
        .data(LikesCountResponse.Data.builder()
            .likes(likeCount)
            .build())
        .build();
  }

  private CommonResponseDTO<LikeData> getLikesResponse(Long id, LikeType type) {
    List<Long> likesUsersList = likeRepository.findLikers(id, type.getType());
    return CommonResponseDTO.<LikeData>builder()
        .data(LikeData.builder()
            .likes(likesUsersList.size())
            .users(likesUsersList)
            .build())
        .build();
  }

}
