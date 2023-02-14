package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;
import ru.skillbox.zerone.backend.model.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class CommentDTO {
  private Integer parentId;
  private String commentText;
  private int id;
  private int postId;
  private LocalDateTime timestamp = LocalDateTime.now();
  private boolean isBlocked;
  private boolean isDeleted;
  private List<CommentDTO> subComments;
  private UserDTO author;
  private int likes;
  private boolean myLike;
  private List<ImageDto> images;
}
