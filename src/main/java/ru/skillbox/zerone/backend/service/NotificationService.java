package ru.skillbox.zerone.backend.service;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.exception.DialogException;
import ru.skillbox.zerone.backend.exception.FriendsAdditionException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.NotificationDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.socket.response.SocketNotificationDataDTO;
import ru.skillbox.zerone.backend.model.entity.*;
import ru.skillbox.zerone.backend.model.enumerated.CommentType;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;
import ru.skillbox.zerone.backend.repository.*;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.zerone.backend.model.enumerated.NotificationType.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final String POST_NOT_FOUND = "Пост с id = \"%s\" не найден";
  private static final String COMMENT_NOT_FOUND = "Комментарий с id = \"%s\" не найден";
  private static final String DIALOG_NOT_FOUND = "Диалог с сообщением с id = \"%s\" не найден";
  private static final String FRIENDSHIP_NOT_FOUND = "Дружба с id = \"%s\" не найдена";
  private final UserMapper userMapper;
  private final NotificationRepository notificationRepository;
  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final MessageRepository messageRepository;
  private final DialogRepository dialogRepository;
  private final FriendshipRepository friendshipRepository;
  private final SocketIOServer socketIOServer;
  private final SocketIORepository socketIORepository;
  private final SocketIOService socketIOService;

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
        author = postRepository.findById(entityId).orElseThrow(
                () -> new PostNotFoundException(String.format(POST_NOT_FOUND, entityId)))
            .getAuthor();
      }
      case POST_COMMENT -> {
        builder.currentEntityId(entityId);
        author = commentRepository.findById(entityId).orElseThrow(
                () -> getCommentException(entityId))
            .getAuthor();
      }
      case COMMENT_COMMENT -> {
        builder.currentEntityId(entityId);
        Comment comment = commentRepository.findById(entityId).orElseThrow(
            () -> getCommentException(entityId));
        author = comment.getAuthor();
        if (comment.getParent() != null) {
          builder.parentEntityId(comment.getParent().getId());
        } else {
          builder.parentEntityId(comment.getId());
        }
      }
      case MESSAGE -> {
        Dialog dialog = dialogRepository.findByMessageId(entityId)
            .orElseThrow(() -> new DialogException(String.format(DIALOG_NOT_FOUND, entityId)));
        builder.parentEntityId(dialog.getId());
        author = notification.getPerson().getId().equals(dialog.getRecipient().getId()) ?
            dialog.getSender() :
            dialog.getRecipient();
      }
      case FRIEND_REQUEST -> {
        Friendship friendship = friendshipRepository.findById(entityId)
            .orElseThrow(() -> new FriendsAdditionException(
                String.format(FRIENDSHIP_NOT_FOUND, entityId)));
        author = notification.getPerson().getId().equals(friendship.getDstPerson().getId()) ?
            friendship.getSrcPerson() :
            friendship.getDstPerson();
      }
      default -> author = null;
    }
    builder.entityAuthor(userMapper.userToUserDTO(author));

    return builder.build();
  }

  private CommentNotFoundException getCommentException(Long entityId) {
    return new CommentNotFoundException(String.format(COMMENT_NOT_FOUND, entityId));
  }

  public void savePost(Post post) {
    List<Friendship> friendships = friendshipRepository
        .findAllBySrcPersonAndStatus(post.getAuthor(), FriendshipStatus.FRIEND);
    List<Notification> notifications = new ArrayList<>();
    friendships.forEach(fr -> {
      notifications.add(Notification.builder()
          .type(POST)
          .person(fr.getDstPerson())
          .entityId(fr.getId())
          .build());
    });
    notificationRepository.saveAll(notifications);
  }

  public void saveComment(Comment comment) {
    if (comment.getType().equals(CommentType.POST)) {
      savePostComment(comment);
    } else {
      saveCommentComment(comment);
    }
  }

  private void savePostComment(Comment comment) {
    Notification notification = notificationRepository.save(Notification.builder()
        .type(POST_COMMENT)
        .person(comment.getPost().getAuthor())
        .entityId(comment.getId())
        .build());
    notificationRepository.save(notification);

    var dataDTO = prepareSocketNotificationDataDTO(notification, comment);

    socketIOService.sendEventToPerson(notification.getPerson(),
        "comment-notification-response", dataDTO);
  }

  private void saveCommentComment(Comment comment) {
    List<User> dstPersons = new ArrayList<>();
    dstPersons.add(comment.getPost().getAuthor());
    User parentAuthor = comment.getParent().getAuthor();
    if (!parentAuthor.equals(dstPersons.get(0))) {
      dstPersons.add(parentAuthor);
    }
    List<Notification> notifications = new ArrayList<>();
    dstPersons.forEach(dstPerson -> {
      notifications.add(Notification.builder()
          .type(COMMENT_COMMENT)
          .person(dstPerson)
          .entityId(comment.getId())
          .build());
    });
    notificationRepository.saveAll(notifications);

    notifications.forEach(notification -> {
      var dataDTO = prepareSocketNotificationDataDTO(notification, comment);
      socketIOService.sendEventToPerson(
          notification.getPerson(), "comment-notification-response", dataDTO);
    });
  }

  private SocketNotificationDataDTO prepareSocketNotificationDataDTO(
      Notification notification, Comment comment) {
    Long parentId = comment.getType().equals(POST) ?
        comment.getId() : comment.getParent().getId();
    var dataDTO = SocketNotificationDataDTO.builder()
        .id(notification.getId())
        .eventType(notification.getType())
        .sentTime(notification.getSentTime().atZone(ZoneId.systemDefault()).toInstant())
        .entityId(comment.getPost().getId())
        .entityAuthor(userMapper.userToUserDTO(comment.getAuthor()))
        .parentId(parentId)
        .currentEntityId(notification.getEntityId())
        .build();
    return dataDTO;
  }

  public void saveFriendship(User dstPerson, Friendship friendship) {
    var notification = Notification.builder()
        .type(FRIEND_REQUEST)
        .person(dstPerson)
        .entityId(friendship.getId())
        .build();
    notificationRepository.save(notification);
  }

  public void saveMessage(Message message) {
    var dstPerson = message.getDialog().getRecipient().getId().equals(message.getAuthor().getId()) ?
        message.getDialog().getSender() :
        message.getDialog().getRecipient();
    var notification = Notification.builder()
        .type(MESSAGE)
        .person(dstPerson)
        .entityId(message.getId())
        .build();
    notificationRepository.save(notification);
  }

}
