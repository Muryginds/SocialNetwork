package ru.skillbox.zerone.backend.tests;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.entity.*;
import ru.skillbox.zerone.backend.model.enumerated.AdminType;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;
import ru.skillbox.zerone.backend.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EntityTests {
  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final DialogRepository dialogRepository;
  private final MessageRepository messageRepository;
  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final PostToTagRepository postToTagRepository;
  private final PostFileRepository postFileRepository;
  private final LikeRepository likeRepository;
  private final FriendshipStatusRepository friendshipStatusRepository;
  private final FriendshipRepository friendshipRepository;
  private final CommentRepository commentRepository;
  private final NotificationSettingRepository notificationSettingRepository;
  private final NotificationRepository notificationRepository;
  private final BlockHistoryRepository blockHistoryRepository;
  private final SupportRequestRepository supportRequestRepository;

  @Bean
  public ApplicationRunner Tests() {
    return args -> {
      userTest();
      adminTest();
      messageAndDialogTest();
      postTest();
      postAndPostToTagTest();
      postFileTest();
      likeTest();
      friendshipAndFriendshipStatusTest();
      commentTest();
      notificationSettingTest();
      notificationTest();
      blockHistoryTest();
      supportRequestTest();
    };
  }

  private void userTest() {
    User user = User.builder()
        .firstName("Vladimir")
        .lastName("Panfilov")
//          .regDate(LocalDateTime.now())
        .birthDate(LocalDate.now())
        .email("vrpanfilov@yandex.ru")
        .phone("+9051234567")
        .password("A password code")
//          .photo("SomeUri")
        .about("Что-то о себе")
//          .status(UserStatus.INACTIVE)
        .country("Россия")
        .city("Москва")
        .confirmationCode("Confirmation code")
//          .messagePermissions(MessagePermissions.ALL)
//          .isApproved(true)
        .lastOnlineTime(LocalDateTime.now())
//          .isBlocked(false)
//          .isDeleted(true)
        .build();
    userRepository.saveAndFlush(user);

    user = User.builder()
        .firstName("Vasiliy")
        .lastName("Andreyev")
//          .regDate(LocalDateTime.now())
        .birthDate(LocalDate.now())
        .email("vasiliy@mail.ru")
        .phone("+9057654321")
        .password("The password code")
//          .photo("SomeUri")
        .about("Я Вася")
//          .status(UserStatus.INACTIVE)
//        .country("Россия")
        .city("Москва")
        .confirmationCode("Confirmation code")
//          .messagePermissions(MessagePermissions.ALL)
//          .isApproved(true)
        .lastOnlineTime(LocalDateTime.now())
//          .isBlocked(false)
        .isDeleted(true)
        .build();
    userRepository.saveAndFlush(user);

    var users = userRepository.findAll();
    if (users.size() != 2) {throw new RuntimeException("users.size() != 2");}
    var userFound = userRepository.findById(2L).get();
    if (!userFound.getLastName().equals(user.getLastName())) {throw new RuntimeException("!userFound.getLastName().equals(user.getLastName())");}
    users = users;
  }

  private void adminTest() {
    Admin admin = Admin.builder()
        .name("Alex")
        .email("alex@mail.ru")
        .password("password")
//          .type(UserType.ADMIN)
        .build();
    adminRepository.saveAndFlush(admin);

    var byId = adminRepository.findById(1L).get();
    if (!byId.getType().equals(AdminType.MODERATOR)) {throw new RuntimeException("!byId.getType().equals(admin.getType())");}
  }

  private void messageAndDialogTest() {
    User sender = userRepository.findById(1L).get();
    User recipient = userRepository.findById(2L).get();

    Dialog dialog = Dialog.builder()
        .sender(sender)
        .recipient(recipient)
        .build();
    dialogRepository.saveAndFlush(dialog);

    Message message = Message.builder()
        .dialog(dialog)
        .messageText("Сообщение")
        .build();
    messageRepository.saveAndFlush(message);

    messageRepository.findById(1L)
        .ifPresentOrElse(m -> {
              System.out.println("message: '" + m.getMessageText() + "'");
            },
            () -> {throw new RuntimeException("message doesn't present");}
        );
  }

  private void postTest() {
    User author = userRepository.findById(2L).get();
    Post post = Post.builder()
        .author(author)
        .title("Всё о слонах")
        .postText("HTML-текст поста")
        .build();
    postRepository.saveAndFlush(post);

    postRepository.findById(1L)
        .ifPresentOrElse(p -> {
              System.out.println("post");
              if (p.getIsBlocked() || p.getIsDeleted()) {
                throw new RuntimeException("p.getIsBlocked() или " +
                    "p.getIsDeleted() не принимают значение по умолчанию");
              }
            },
            () -> {throw new RuntimeException("message doesn't present");}
        );
  }

  private void postAndPostToTagTest() {
    Tag tag = Tag.builder()
        .tag("TagName")
        .build();
    tagRepository.saveAndFlush(tag);

    Post post = postRepository.findById(1L).get();

    PostToTag postToTag = PostToTag.builder()
        .post(post)
        .tag(tag)
        .build();
    postToTagRepository.saveAndFlush(postToTag);

    Tag tagDb = postToTagRepository.findTagsByPost(post).get(0);
    if (!tagDb.getTag().equals(tag.getTag())) {
      throw new RuntimeException("Ошибка при поиске tag в PostToTag");
    }
    System.out.println(tagDb);

    Post postDb = postToTagRepository.findPostsByTag(tag).get(0);
    if (!postDb.getPostText().equals(post.getPostText())) {
      throw new RuntimeException("Ошибка при поиске post в PostToTag");
    }
    System.out.println(postDb);
  }

  private void postFileTest() {
    PostFile postFile = PostFile.builder()
        .post(postRepository.findById(1L).get())
        .path("c:\\forlder\\file.txt")
        .build();
    postFileRepository.saveAndFlush(postFile);

    postFileRepository.findById(1L)
        .ifPresentOrElse(pf -> {
              if (pf.getPath().equals(postFile.getPath())) {
                System.out.println(pf);
              }
            },
            () -> {
              throw new RuntimeException("Ошибка при извлечении postFile");
            });
  }

  private void likeTest() {
    User user = userRepository.findById(1L).get();

    Like like = Like.builder()
        .entityId(1L)
        .user(user)
        .build();
    likeRepository.saveAndFlush(like);

    likeRepository.findById(1L)
        .ifPresentOrElse(l -> {
              if (l.getUser().getFirstName().equals(user.getFirstName())) {
                System.out.println(l);
              }
            },
            () -> {
              throw new RuntimeException("Ошибка при извлечении like");
            });
  }

  @Transactional
  private void friendshipAndFriendshipStatusTest() {
    User srcPerson = userRepository.findById(1L).get();
    User dstPerson = userRepository.findById(2L).get();


    FriendshipStatus friendshipStatus = FriendshipStatus.builder()
        .name(srcPerson.getLastName() + " запросил дружбу с " + dstPerson.getLastName())
        .build();
    friendshipStatusRepository.saveAndFlush(friendshipStatus);

    Friendship friendship = Friendship.builder()
        .status(friendshipStatus)
        .srcPerson(srcPerson)
        .dstPerson(dstPerson)
        .build();
    friendshipRepository.saveAndFlush(friendship);

    System.out.println(friendshipStatus.getName());
  }

  private void commentTest() {
    User author1 = userRepository.findById(1L).get();
    User author2 = userRepository.findById(2L).get();
    Post post = postRepository.findById(1L).get();
    Comment parentComment = Comment.builder()
        .post(post)
        .author(author1)
        .commentText("Родительский комментарий")
        .build();
    commentRepository.saveAndFlush(parentComment);

    Comment subComment = Comment.builder()
        .post(post)
        .parent(parentComment)
        .author(author2)
        .commentText("Подкомментарий")
        .build();
    commentRepository.saveAndFlush(subComment);

    System.out.println("Сохранены два комментария");

  }

  private void notificationSettingTest() {
    User user = userRepository.findById(2L).get();
    NotificationSetting setting = NotificationSetting.builder()
        .user(user)
//        .postEnabled(false)
        .build();
    notificationSettingRepository.saveAndFlush(setting);

    notificationSettingRepository.findById(1L)
        .ifPresentOrElse(ns -> {
              System.out.println("Сохранено и считано notificationSetting");
            },
            () -> {
              throw new RuntimeException("notificationSetting не прочитаны");
            });
  }

  private void notificationTest() {
    NotificationSetting setting = notificationSettingRepository.findById(1L).get();
    User person = userRepository.findById(2L).get();

    Notification notification = Notification.builder()
        .typeId(NotificationType.POST)
        .person(person)
        .entityId(1L)
        .build();

    boolean enabled = false;
    switch (notification.getTypeId()) {
      case POST -> enabled = setting.getPostEnabled();
      case COMMENT_COMMENT -> enabled = setting.getCommentCommentEnabled();
      case MESSAGE -> enabled = setting.getMessagesEnabled();
      case FRIEND_BIRTHDAY -> enabled = setting.getFriendBirthdayEnabled();
      case FRIEND_REQUEST -> enabled = setting.getFriendRequestEnabled();
      case POST_COMMENT -> enabled = setting.getPostCommentEnabled();
    }
    if (!enabled) {
      throw new RuntimeException("Неразрешённое сообщение");
    }
    notificationRepository.saveAndFlush(notification);
    if (notification.getId() != 1) {
      throw new RuntimeException("Уведомление не было сохранено");
    }

    System.out.println("Отослано уведомление POST на пост " + notification.getEntityId());
  }

  private void blockHistoryTest() {
    BlockHistory blockHistory = BlockHistory.builder()
        .user(userRepository.findById(1L).get())
        .post(postRepository.findById(1L).get())
        .comment(commentRepository.findById(1L).get())
        .build();
    blockHistoryRepository.saveAndFlush(blockHistory);

    blockHistoryRepository.findById(1L)
        .ifPresentOrElse(bh -> {
              System.out.println("Считано " + bh.toString());
            },
            () -> {
              throw new RuntimeException("blockHistory не считано");
            });
  }

  private void supportRequestTest() {
    SupportRequest supportRequest = SupportRequest.builder()
        .firstName("Степан")
        .lastName("Иванов")
        .email("stepan-ivanov@mail.ru")
        .message("Сообщение в службу поддержки")
        .build();
    supportRequestRepository.saveAndFlush(supportRequest);

    supportRequestRepository.findById(1L)
        .ifPresentOrElse(sr -> {
              System.out.println("Считано " + sr.toString());
            },
            () -> {
              throw new RuntimeException("supportRequest не считано");
            });
  }
}
