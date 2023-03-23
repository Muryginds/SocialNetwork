package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.NotificationDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.entity.*;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;
import ru.skillbox.zerone.backend.repository.*;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.zerone.backend.model.enumerated.NotificationType.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final UserMapper userMapper;
  private final NotificationRepository notificationRepository;
  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final MessageRepository messageRepository;
  private final DialogRepository dialogRepository;

  public CommonListResponseDTO<NotificationDTO> getNotifications(
      int offset, int itemPerPage) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Notification> pageableNotifications =
        notificationRepository.findAllByPersonAndStatus(user, ReadStatus.SENT, pageable);

    List<NotificationDTO> dtoList = new ArrayList<>();
    pageableNotifications.stream()
        .forEach(notification -> dtoList.add(notificationToNotificationDTO(notification)));

    var dtos = CommonListResponseDTO.<NotificationDTO>builder()
        .total(pageableNotifications.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(dtoList)
        .build();
    return dtos;
  }

  public static Long getCurrentEntityId(Notification notification) {
    switch (notification.getType()) {
      case POST_COMMENT, COMMENT_COMMENT -> {
        return notification.getEntityId();
      }
    }
    return null;
  }

  public Long getParentEntityId(Notification notification) {
    switch (notification.getType()) {
      case POST_COMMENT, COMMENT_COMMENT -> {
        Optional<Comment> commentOpt = commentRepository.findById(notification.getEntityId());
        if (commentOpt.isPresent()) {
          var comment = commentOpt.get();
          if (comment.getParent() != null) {
            return comment.getParent().getId();
          }
          return comment.getId();
        }
      }
      case MESSAGE -> {
        return notification.getEntityId();
      }
    }
    return null;
  }

  public CommonListResponseDTO<NotificationDTO> putNotifications(int offset, int itemPerPage, int id, boolean all) {
    return new CommonListResponseDTO<>();
  }

  private NotificationDTO notificationToNotificationDTO(Notification notification) {
    Long entityId = notification.getEntityId();
    var builder = NotificationDTO.builder();
    builder
        .id(notification.getId())
        .eventType(notification.getType())
        .sentTime(notification.getSentTime())
        .entityId(entityId);
    User author;
    switch (notification.getType()) {
      case POST -> {
        author = postRepository.findById(entityId).get().getAuthor();
      }
      case POST_COMMENT -> {
        builder.currentEntityId(entityId);
        author = commentRepository.findById(entityId).get().getAuthor();
      }
      case COMMENT_COMMENT -> {
        builder.currentEntityId(entityId);
        Comment comment = commentRepository.findById(entityId).get();
        author = comment.getAuthor();
        if (comment.getParent() != null) {
          builder.parentEntityId(comment.getParent().getId());
        } else {
          builder.parentEntityId(comment.getId());
        }
      }
      case MESSAGE -> {
        Dialog dialog = dialogRepository.findByMessageId(entityId).get();
        builder.parentEntityId(dialog.getId());
        author = notification.getPerson().getId().equals(dialog.getRecipient().getId()) ?
            dialog.getSender() :
            dialog.getRecipient();
      }
      case FRIEND_REQUEST -> {
        author = null;
      }
      default -> author = null;
    }
    builder.entityAuthor(userMapper.userToUserDTO(author));

    return builder.build();
  }

//  public UserDTO getEntityAuthorDTO(Notification notification) {
//    return userMapper.userToUserDTO(notification.getPerson());
//  }

  public void savePost(Post post) {
    var notification = Notification.builder()
        .type(POST)
        .person(post.getAuthor())
        .entityId(post.getId())
        .build();
    notificationRepository.save(notification);
  }

  public void savePostComment(Comment comment) {
    var notification = Notification.builder()
        .type(POST_COMMENT)
        .person(comment.getPost().getAuthor())
        .entityId(comment.getId())
        .build();
    notificationRepository.save(notification);
  }

  public void saveCommentComment(Comment comment) {
    var notification = Notification.builder()
        .type(COMMENT_COMMENT)
        .person(comment.getParent().getAuthor())
        .entityId(comment.getId())
        .build();
    notificationRepository.save(notification);
  }

  public void saveFriendship(User srcPerson, User dstPerson) {
    var notification = Notification.builder()
        .type(FRIEND_REQUEST)
        .person(srcPerson)
        .entityId(dstPerson.getId())
        .build();
    notificationRepository.save(notification);
  }

  public void saveMessage(User dstPerson, Message message) {
    var notification = Notification.builder()
        .type(FRIEND_REQUEST)
        .person(dstPerson)
        .entityId(message.getId())
        .build();
    notificationRepository.save(notification);
  }
}
