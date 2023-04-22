package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.FriendshipException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.IsFriendsDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.model.entity.Friendship;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus;
import ru.skillbox.zerone.backend.repository.FriendshipRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import ru.skillbox.zerone.backend.util.ResponseUtils;

import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus.*;

@Service
@RequiredArgsConstructor
public class FriendService {
  public static final String CANNOT_ADD_BLOCKED_USER =
      "Вы не можете добавить в друзья заблокированного администрацией пользователя";
  public static final String CANNOT_ADD_DELETED_USER =
      "Вы не можете добавить в друзья удаленного пользователя";
  public static final String CANNOT_ADD_NOT_APPROVED_USER =
      "Вы не можете добавить в друзья пользователя, который не подтвердил учетную запись";
  public static final String CANNOT_ADD_YOURSELF =
      "Вы не можете добавить в друзья самого(саму) себя";
  public static final String NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN = "Нет пары к записи с id: %s";
  public static final String USERS_ARE_NOT_FRIENDS = "Пользователи не друзья";
  public static final String WRONG_STATUS_COMBINATION = "Неверная комбинация статусов: %s, %s";
  public static final String DEADLOCK_BLOCK = "У вас взаимная блокировка с пользователем";
  public static final String YOU_HAVE_ALREADY_BLOCKED_HIM = "Вы уже заблокировали пользователя";
  public static final String USER_IS_IN_FRIENDS_ALREADY = "Пользователь уже в друзьях";
  public static final String YOU_HAVE_SENT_REQUEST_ALREADY = "Вы уже отправили заявку в друзья";
  public static final String YOU_WAS_BLOCKED_BY_USER = "Вы были заблокированы пользователем";
  public static final String USER_NOT_BLOCKED = "Пользователь не заблокирован";

  private final FriendshipRepository friendshipRepository;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final NotificationService notificationService;

  @Transactional
  @SuppressWarnings({"OptionalGetWithoutIsPresent", "java:S3655", "DuplicatedCode"})
  public CommonResponseDTO<MessageResponseDTO> addFriend(long id) {
    var friend = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    checkUserNotAllowedToAddFriendsAndThrowExceptionIfMatched(friend);

    var user = CurrentUserUtils.getCurrentUser();

    if (user.getId().equals(friend.getId())) {
      throw new FriendshipException(CANNOT_ADD_YOURSELF);
    }

    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, friend);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(friend, user);

    checkIfOneOptionalEmptyAndOneNotEmptyAndThrowExceptionIfMatched(friendshipOptional, reversedFriendshipOptional);

    List<Friendship> friendshipList = new ArrayList<>();

    if (isBothOptionalEmpty(friendshipOptional, reversedFriendshipOptional)) {
      friendshipList.addAll(createNewFriendshipRequest(user, friend));
    }

    if (isBothOptionalPresent(friendshipOptional, reversedFriendshipOptional)) {
      friendshipList.addAll(updateFriendshipRequest(friendshipOptional.get(), reversedFriendshipOptional.get()));
    }

    friendshipRepository.saveAll(friendshipList);

    notificationService.saveFriendship(friendshipList);

    return ResponseUtils.commonResponseDataOk();
  }

  private void checkIfOneOptionalEmptyAndOneNotEmptyAndThrowExceptionIfMatched(Optional<Friendship> optionalOne,
                                                                               Optional<Friendship> optionalTwo) {
    Long id = null;
    if (optionalOne.isPresent() && optionalTwo.isEmpty()) {
      id = optionalOne.get().getId();
    }
    if (optionalOne.isEmpty() && optionalTwo.isPresent()) {
      id = optionalTwo.get().getId();
    }
    if (Objects.nonNull(id)) {
      throw new FriendshipException(String.format(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN, id));
    }
  }

  private void checkUserNotAllowedToAddFriendsAndThrowExceptionIfMatched(User user) {
    if (TRUE.equals(user.getIsBlocked())) {
      throw new FriendshipException(CANNOT_ADD_BLOCKED_USER);
    }

    if (TRUE.equals(user.getIsDeleted())) {
      throw new FriendshipException(CANNOT_ADD_DELETED_USER);
    }

    if (FALSE.equals(user.getIsApproved())) {
      throw new FriendshipException(CANNOT_ADD_NOT_APPROVED_USER);
    }
  }

  private List<Friendship> updateFriendshipRequest(Friendship friendship, Friendship reversedFriendship) {
    List<Friendship> friendshipList = new ArrayList<>();

    var friendshipStatus = friendship.getStatus();
    var reversedFriendshipStatus = reversedFriendship.getStatus();

    checkNotAllowedStatusCombinationsAndThrowExceptionIfMatched(friendshipStatus, reversedFriendshipStatus);

    if (friendshipStatus.equals(REQUEST) && reversedFriendshipStatus.equals(SUBSCRIBED) ||
        friendshipStatus.equals(DECLINED) && reversedFriendshipStatus.equals(SUBSCRIBED)) {
      friendship.setStatus(FRIEND);
      friendshipList.add(friendship);
      reversedFriendship.setStatus(FRIEND);
      friendshipList.add(reversedFriendship);
      return friendshipList;
    }

    if (friendshipStatus.equals(SUBSCRIBED) && reversedFriendshipStatus.equals(DECLINED)) {
      reversedFriendship.setStatus(REQUEST);
      friendshipList.add(reversedFriendship);
      return friendshipList;
    }

    throw new FriendshipException(String.format(WRONG_STATUS_COMBINATION, friendshipStatus, reversedFriendshipStatus));
  }

  private void checkNotAllowedStatusCombinationsAndThrowExceptionIfMatched(FriendshipStatus friendshipStatus, FriendshipStatus reversedFriendshipStatus) {
    if (friendshipStatus.equals(DEADLOCK) && reversedFriendshipStatus.equals(DEADLOCK)) {
      throw new FriendshipException(DEADLOCK_BLOCK);
    }

    if (friendshipStatus.equals(BLOCKED) && reversedFriendshipStatus.equals(WASBLOCKEDBY)) {
      throw new FriendshipException(YOU_HAVE_ALREADY_BLOCKED_HIM);
    }

    if (friendshipStatus.equals(WASBLOCKEDBY) && reversedFriendshipStatus.equals(BLOCKED)) {
      throw new FriendshipException(YOU_WAS_BLOCKED_BY_USER);
    }

    if (friendshipStatus.equals(FRIEND) && reversedFriendshipStatus.equals(FRIEND)) {
      throw new FriendshipException(USER_IS_IN_FRIENDS_ALREADY);
    }

    if (friendshipStatus.equals(SUBSCRIBED) && reversedFriendshipStatus.equals(REQUEST)) {
      throw new FriendshipException(YOU_HAVE_SENT_REQUEST_ALREADY);
    }
  }

  @Transactional
  @SuppressWarnings({"OptionalGetWithoutIsPresent", "java:S3655"})
  public CommonResponseDTO<MessageResponseDTO> removeFriend(long id) {
    var friend = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    var user = CurrentUserUtils.getCurrentUser();

    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, friend);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(friend, user);

    checkIfOneOptionalEmptyAndOneNotEmptyAndThrowExceptionIfMatched(friendshipOptional, reversedFriendshipOptional);

    if (isBothOptionalEmpty(friendshipOptional, reversedFriendshipOptional)) {
      throw new FriendshipException(USERS_ARE_NOT_FRIENDS);
    }

    if (isBothOptionalPresent(friendshipOptional, reversedFriendshipOptional)) {
      var friendship = friendshipOptional.get();
      var reversedFriendship = reversedFriendshipOptional.get();
      var friendshipStatus = friendship.getStatus();
      var reversedFriendshipStatus = reversedFriendship.getStatus();

      if (!(friendshipStatus.equals(FRIEND) && reversedFriendshipStatus.equals(FRIEND))) {
        throw new FriendshipException(USERS_ARE_NOT_FRIENDS);
      }

      friendship.setStatus(DECLINED);
      reversedFriendship.setStatus(SUBSCRIBED);

      friendshipRepository.saveAll(List.of(friendship, reversedFriendship));
    }

    return ResponseUtils.commonResponseDataOk();
  }

  private List<Friendship> createNewFriendshipRequest(User user, User friend) {
    var newFriendship = Friendship.builder()
        .srcPerson(user)
        .dstPerson(friend)
        .status(SUBSCRIBED)
        .build();
    var reversedFriendship = Friendship.builder()
        .srcPerson(friend)
        .dstPerson(user)
        .status(REQUEST)
        .build();

    return List.of(newFriendship, reversedFriendship);
  }

  private boolean isBothOptionalPresent(Optional<Friendship> optionalOne, Optional<Friendship> optionalTwo) {
    return optionalOne.isPresent() && optionalTwo.isPresent();
  }

  private boolean isBothOptionalEmpty(Optional<Friendship> optionalOne, Optional<Friendship> optionalTwo) {
    return optionalOne.isEmpty() && optionalTwo.isEmpty();
  }

  public CommonListResponseDTO<UserDTO> getFriendList(String name, int offset, int itemPerPage) {
    var friendshipPage = getPageOfFriendsByFriendStatus(FRIEND, name, offset, itemPerPage);
    var friends = friendshipPage.map(Friendship::getDstPerson).toList();

    return CommonListResponseDTO.<UserDTO>builder()
        .total(friendshipPage.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(userMapper.usersToUserDTO(friends))
        .build();
  }

  public CommonListResponseDTO<UserDTO> getFriendRequestList(String name, int offset, int itemPerPage) {
    var friendshipPage = getPageOfFriendsByFriendStatus(REQUEST, name, offset, itemPerPage);
    var friends = friendshipPage.map(Friendship::getDstPerson).toList();

    return CommonListResponseDTO.<UserDTO>builder()
        .total(friendshipPage.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(userMapper.usersToUserDTO(friends))
        .build();
  }

  private Page<Friendship> getPageOfFriendsByFriendStatus(FriendshipStatus status, String name, int offset, int itemPerPage) {
    var user = CurrentUserUtils.getCurrentUser();
    var pageRequest = PageRequest.of(offset / itemPerPage, itemPerPage);

    if (name.isBlank()) {
      return friendshipRepository.findAllBySrcPersonAndStatus(user, status, pageRequest);
    } else {
      return friendshipRepository.findAllBySrcPersonAndStatusAndDstPersonNameLike(user, status, name, pageRequest);
    }
  }

  public CommonListResponseDTO<StatusFriendDTO> checkIsFriends(IsFriendsDTO isFriendsDTO) {
    var user = CurrentUserUtils.getCurrentUser();

    var friendships = friendshipRepository.findAllBySrcPersonAndDstPersonIdIn(user, isFriendsDTO.getUserIds());

    var statusFriendDTOList = friendships.stream()
        .map(f -> new StatusFriendDTO(f.getDstPerson().getId(), f.getStatus()))
        .toList();

    return CommonListResponseDTO.<StatusFriendDTO>builder()
        .data(statusFriendDTOList)
        .build();
  }

  public CommonListResponseDTO<UserDTO> getRecommendations(int offset, int itemPerPage) {
    return CommonListResponseDTO.<UserDTO>builder()
        .total(0)
        .offset(offset)
        .perPage(itemPerPage)
        .data(Collections.emptyList())
        .build();
  }

  @Transactional
  @SuppressWarnings({"OptionalGetWithoutIsPresent", "java:S3655", "DuplicatedCode"})
  public CommonResponseDTO<MessageResponseDTO> blockUser(long id) {
    var target = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    var user = CurrentUserUtils.getCurrentUser();

    if (user.getId().equals(target.getId())) {
      throw new FriendshipException(CANNOT_ADD_YOURSELF);
    }

    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, target);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(target, user);

    checkIfOneOptionalEmptyAndOneNotEmptyAndThrowExceptionIfMatched(friendshipOptional, reversedFriendshipOptional);

    List<Friendship> friendshipList = new ArrayList<>();

    if (isBothOptionalEmpty(friendshipOptional, reversedFriendshipOptional)) {
      friendshipList.addAll(createNewBlockRecord(user, target));
    }

    if (isBothOptionalPresent(friendshipOptional, reversedFriendshipOptional)) {
      friendshipList.addAll(updateBlockRecord(friendshipOptional.get(), reversedFriendshipOptional.get()));
    }

    friendshipRepository.saveAll(friendshipList);

    return ResponseUtils.commonResponseDataOk();
  }

  private List<Friendship> updateBlockRecord(Friendship friendship, Friendship reversedFriendship) {
    List<Friendship> friendshipList = new ArrayList<>();

    var friendshipStatus = friendship.getStatus();
    var reversedFriendshipStatus = reversedFriendship.getStatus();

    if (friendshipStatus.equals(DEADLOCK) && reversedFriendshipStatus.equals(DEADLOCK) ||
        friendshipStatus.equals(BLOCKED) && reversedFriendshipStatus.equals(WASBLOCKEDBY)) {
      throw new FriendshipException(YOU_HAVE_ALREADY_BLOCKED_HIM);
    }

    if (friendshipStatus.equals(WASBLOCKEDBY) && reversedFriendshipStatus.equals(BLOCKED)) {
      friendship.setStatus(DEADLOCK);
      friendshipList.add(friendship);
      reversedFriendship.setStatus(DEADLOCK);
      friendshipList.add(reversedFriendship);
      return friendshipList;
    }

    friendship.setStatus(BLOCKED);
    friendshipList.add(friendship);
    reversedFriendship.setStatus(WASBLOCKEDBY);
    friendshipList.add(reversedFriendship);
    return friendshipList;
  }

  private List<Friendship> createNewBlockRecord(User user, User friend) {
    var newBlock = Friendship.builder()
        .srcPerson(user)
        .dstPerson(friend)
        .status(BLOCKED)
        .build();
    var reversedBlock = Friendship.builder()
        .srcPerson(friend)
        .dstPerson(user)
        .status(WASBLOCKEDBY)
        .build();

    return List.of(newBlock, reversedBlock);
  }

  @Transactional
  @SuppressWarnings({"OptionalGetWithoutIsPresent", "java:S3655"})
  public CommonResponseDTO<MessageResponseDTO> unblockUser(long id) {
    var target = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    var user = CurrentUserUtils.getCurrentUser();
    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, target);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(target, user);

    checkIfOneOptionalEmptyAndOneNotEmptyAndThrowExceptionIfMatched(friendshipOptional, reversedFriendshipOptional);

    if (isBothOptionalPresent(friendshipOptional, reversedFriendshipOptional)) {
      var friendship = friendshipOptional.get();
      var reversedFriendship = reversedFriendshipOptional.get();
      var friendshipStatus = friendship.getStatus();
      var reversedFriendshipStatus = reversedFriendship.getStatus();

      if (friendshipStatus.equals(DEADLOCK) && reversedFriendshipStatus.equals(DEADLOCK)) {
        friendship.setStatus(WASBLOCKEDBY);
        reversedFriendship.setStatus(BLOCKED);
        friendshipRepository.saveAll(List.of(friendship, reversedFriendship));
        return ResponseUtils.commonResponseDataOk();
      }

      if (friendshipStatus.equals(BLOCKED) && reversedFriendshipStatus.equals(WASBLOCKEDBY)) {
        friendshipRepository.deleteAll(List.of(friendship, reversedFriendship));
        return ResponseUtils.commonResponseDataOk();
      }
    }

    throw new FriendshipException(USER_NOT_BLOCKED);
  }
}