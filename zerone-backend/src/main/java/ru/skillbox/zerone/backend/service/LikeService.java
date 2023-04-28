package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.model.dto.request.LikeRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.LikeData;
import ru.skillbox.zerone.backend.model.dto.response.LikesCountResponse;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.CommentRepository;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.List;

import static ru.skillbox.zerone.backend.model.enumerated.LikeType.COMMENT;
import static ru.skillbox.zerone.backend.model.enumerated.LikeType.POST;

@Service
@AllArgsConstructor
public class LikeService {
  private final LikeRepository likeRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;


  public CommonResponseDTO<LikeData> putLikes(LikeRequestDTO likeRequestDTO) {
    User user = CurrentUserUtils.getCurrentUser();
    if (POST.equals(likeRequestDTO.getType())) {
      Post post = postRepository.findById(likeRequestDTO.getId()).orElseThrow(() -> new PostNotFoundException(likeRequestDTO.getId()));
      if (likeRepository.findByUserAndPost(user, post).isEmpty()) {
        likeRepository.saveAndFlush(Like.builder()
            .type(POST)
            .user(user)
            .post(post)
            .build());
      }
    } else if (COMMENT.equals(likeRequestDTO.getType())) {
      Comment comment = commentRepository.findById(likeRequestDTO.getId()).orElseThrow(() -> new CommentNotFoundException(likeRequestDTO.getId()));
      if (likeRepository.findByUserAndComment(user, comment).isEmpty()) {
        likeRepository.saveAndFlush(Like.builder()
            .type(COMMENT)
            .user(user)
            .comment(comment)
            .build());
      }
    }
    return getLikesResponse(likeRequestDTO.getId(), likeRequestDTO.getType().toString());
  }

  public CommonResponseDTO<LikeData> getLikesResponse(Long id, String type) {

    List<Long> likesUsersList = likeRepository.findLikers(id, type);
    return CommonResponseDTO.<LikeData>builder()
        .data(LikeData.builder()
            .likes(likesUsersList.size())
            .users(likesUsersList)
            .build())
        .build();
  }

  public CommonResponseDTO<LikeData> getLikes(Long id, String type) {
    return getLikesResponse(id, type);
  }

  public LikesCountResponse deleteLike(Long id, String type) {
    User user = CurrentUserUtils.getCurrentUser();
    if (POST.toString().equals(type)) {
      Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
      likeRepository.findByUserAndPost(user, post).ifPresent(likeRepository::delete);

      return LikesCountResponse.builder()
          .data(LikesCountResponse.Data.builder()
              .likes(likeRepository.countByPost(post))
              .build())
          .build();

    } else if (COMMENT.toString().equals(type)) {

      Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));

      likeRepository.findByUserAndComment(user, comment).ifPresent(likeRepository::delete);

      return LikesCountResponse.builder()
          .data(LikesCountResponse.Data.builder()
              .likes(likeRepository.countByComment(comment))
              .build())
          .build();
    }
    return LikesCountResponse.builder()
        .error("wrong param type")
        .data(LikesCountResponse.Data.builder()
            .likes(0)
            .build())
        .build();
  }
}
