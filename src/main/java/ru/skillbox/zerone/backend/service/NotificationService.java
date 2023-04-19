package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.*;
import ru.skillbox.zerone.backend.mapstruct.SocketUserMapper;
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
import java.util.ArrayList;
import java.util.List;

import static ru.skillbox.zerone.backend.model.enumerated.NotificationType.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final String POST_NOT_FOUND = "Пост с id = %s не найден";
  private static final String COMMENT_NOT_FOUND = "Комментарий с id = %s не найден";
  private static final String DIALOG_NOT_FOUND = "Диалог с сообщением с id = %s не найден";
  private static final String FRIENDSHIP_NOT_FOUND = "Дружба с id = %s не найдена";
  private final UserMapper userMapper;
  private final SocketUserMapper socketUserMapper;
  private final NotificationRepository notificationRepository;
  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final DialogRepository dialogRepository;
  private final FriendshipRepository friendshipRepository;
  private final SocketIOService socketIOService;
  private final NotificationSettingService notificationSettingService;

  public CommonListResponseDTO<NotificationDTO> getNotifications(
      int offset, int itemPerPage) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Notification> pageableNotifications =
        notificationRepository.findAllByPersonAndStatus(user, ReadStatus.SENT, pageable);

    List<NotificationDTO> dtoList = new ArrayList<>();
    pageableNotifications.stream()
        .forEach(notification -> dtoList.add(notificationToNotificationDTO(notification)));

    return CommonListResponseDTO.<NotificationDTO>builder()
        .total(pageableNotifications.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(dtoList)
        .build();
  }

  public CommonListResponseDTO<NotificationDTO> putNotifications(int offset, int itemPerPage, long id, boolean all) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    List<Notification> notifications = new ArrayList<>();
    long totalElements;
    if (id > 0) {
      Notification byId = notificationRepository.findById(id)
          .orElseThrow(() -> new NotificationException("Нет уведомления с id = " + id));
      notifications.add(byId);
      totalElements = 1L;
    } else if (all) {
      Page<Notification> pages =
          notificationRepository.findAllByPersonAndStatus(user, ReadStatus.SENT, pageable);
      notifications.addAll(pages.getContent());
      totalElements = pages.getTotalElements();
    } else {
      throw new NotificationException("Неверные параметры");
    }
    notifications.forEach(n -> n.setStatus(ReadStatus.READ));
    notificationRepository.saveAll(notifications);

    List<NotificationDTO> dtoList = new ArrayList<>();
    notifications.forEach(notification -> dtoList.add(notificationToNotificationDTO(notification)));

    return CommonListResponseDTO.<NotificationDTO>builder()
        .total(totalElements)
        .perPage(itemPerPage)
        .offset(offset)
        .data(dtoList)
        .build();
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
      case POST -> author = postRepository.findById(entityId).orElseThrow(
              () -> new PostNotFoundException(String.format(POST_NOT_FOUND, entityId)))
          .getAuthor();
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
            .orElseThrow(() -> new FriendshipException(
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
    friendships.forEach(fr ->
        notifications.add(Notification.builder()
            .type(POST)
            .person(fr.getDstPerson())
            .entityId(fr.getId())
            .build())
    );
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
        .entityId(comment.getPost().getId())
        .build());
    notificationRepository.save(notification);

    var dataDTO = prepareSocketNotificationDataDTOForComments(notification, comment);

    socketIOService.sendEventToPerson(notification.getPerson(),
        "comment-notification-response", dataDTO);
  }

  private void saveCommentComment(Comment comment) {
    Notification notification = Notification.builder()
        .type(COMMENT_COMMENT)
        .person(comment.getParent().getAuthor())
        .entityId(comment.getId())
        .build();
    notificationRepository.save(notification);
    var dataDTO = prepareSocketNotificationDataDTOForComments(notification, comment);
    socketIOService.sendEventToPerson(
        notification.getPerson(), "comment-notification-response", dataDTO);
  }

  private SocketNotificationDataDTO prepareSocketNotificationDataDTOForComments(
      Notification notification, Comment comment) {
    Long parentId;
    Long currentEntityId;
    if (comment.getType().equals(CommentType.POST)) {
      parentId = comment.getPost().getId();
      currentEntityId = parentId;
    } else {
      parentId = comment.getParent().getId();
      currentEntityId = comment.getId();
    }
    User author = comment.getAuthor();

    return SocketNotificationDataDTO.builder()
        .id(notification.getId())
        .eventType(notification.getType())
        .sentTime(notification.getSentTime().atZone(ZoneId.systemDefault()).toInstant())
        .entityId(comment.getPost().getId())
        .entityAuthor(socketUserMapper.userToSocketUserDTO(author,
            author.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant(),
            author.getRegDate().atZone(ZoneId.systemDefault()).toInstant(),
            author.getLastOnlineTime().atZone(ZoneId.systemDefault()).toInstant()))
        .parentId(parentId)
        .currentEntityId(currentEntityId)
        .build();
  }

  private void checkFriendshipEnabled(User person) {
    if (notificationSettingService.getSetting(person)
        .getFriendRequestEnabled().equals(Boolean.FALSE)) {
      throw new FriendshipException(String.format(
          "Пользователь %s запретил заявки в друзья", person.getLastName()
      ));
    }
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


  public void saveFriendship(List<Friendship> friendships) {
    User user = CurrentUserUtils.getCurrentUser();
    Friendship friendship = friendships.get(0);

    checkFriendshipEnabled(friendship.getSrcPerson());
    checkFriendshipEnabled(friendship.getDstPerson());

    User dstPerson = friendships.get(0).getDstPerson().getId().equals(user.getId()) ?
        friendship.getSrcPerson() : friendship.getDstPerson();
    var notification = Notification.builder()
        .type(FRIEND_REQUEST)
        .person(dstPerson)
        .entityId(friendship.getId())
        .build();
    notificationRepository.save(notification);

    var dataDTO = SocketNotificationDataDTO.builder()
        .id(notification.getId())
        .eventType(notification.getType())
        .sentTime(notification.getSentTime().atZone(ZoneId.systemDefault()).toInstant())
        .entityId(user.getId())
        .entityAuthor(socketUserMapper.userToSocketUserDTO(user,
            user.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant(),
            user.getRegDate().atZone(ZoneId.systemDefault()).toInstant(),
            user.getLastOnlineTime().atZone(ZoneId.systemDefault()).toInstant()))
        .build();
    socketIOService.sendEventToPerson(notification.getPerson(),
        "friend-notification-response", dataDTO);
  }
}
