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

import static ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus.*;

@Service
@RequiredArgsConstructor
public class FriendService {
  private static final String NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN = "Нет пары к записи с id: %s";
  private final FriendshipRepository friendshipRepository;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  @SuppressWarnings({"Duplicates", "OptionalGetWithoutIsPresent", "java:S3655"})
  public CommonResponseDTO<Object> addFriend(long id) {
    var friend = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    var user = CurrentUserUtils.getCurrentUser();

    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, friend);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(friend, user);

    if (isOneOptionalEmptyAndOneNotEmpty(friendshipOptional, reversedFriendshipOptional)) {
      var identifier = friendshipOptional.orElse(reversedFriendshipOptional.get()).getId();
      throw new FriendshipException(String.format(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN, identifier));
    }

    List<Friendship> friendshipList = new ArrayList<>();

    if (isBothOptionalEmpty(friendshipOptional, reversedFriendshipOptional)) {
      friendshipList.addAll(createNewFriendshipRequest(user, friend));
    }

    if (isBothOptionalPresent(friendshipOptional, reversedFriendshipOptional)) {
      friendshipList.addAll(updateFriendshipRequest(friendshipOptional.get(), reversedFriendshipOptional.get()));
    }

    friendshipRepository.saveAll(friendshipList);

    return CommonResponseDTO.builder()
        .message("ok")
        .build();
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

    throw new FriendshipException(String.format("Неверная комбинация статусов: %s, %s", friendshipStatus, reversedFriendshipStatus));
  }

  private void checkNotAllowedStatusCombinationsAndThrowExceptionIfMatched(FriendshipStatus friendshipStatus, FriendshipStatus reversedFriendshipStatus) {
    if (friendshipStatus.equals(DEADLOCK) && reversedFriendshipStatus.equals(DEADLOCK)) {
      throw new FriendshipException("У вас взаимная блокировка с пользователем");
    }

    if (friendshipStatus.equals(BLOCKED) && reversedFriendshipStatus.equals(WASBLOCKEDBY)) {
      throw new FriendshipException("Вы заблокировали пользователя");
    }

    if (friendshipStatus.equals(WASBLOCKEDBY) && reversedFriendshipStatus.equals(BLOCKED)) {
      throw new FriendshipException("Вы были заблокированы пользователем");
    }

    if (friendshipStatus.equals(FRIEND) && reversedFriendshipStatus.equals(FRIEND)) {
      throw new FriendshipException("Пользователь уже в друзьях");
    }

    if (friendshipStatus.equals(SUBSCRIBED) && reversedFriendshipStatus.equals(REQUEST)) {
      throw new FriendshipException("Вы уже отправили заявку в друзья");
    }
  }

  @Transactional
  @SuppressWarnings({"Duplicates", "OptionalGetWithoutIsPresent", "java:S3655"})
  public CommonResponseDTO<Object> removeFriend(long id) {
    var friend = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    var user = CurrentUserUtils.getCurrentUser();

    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, friend);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(friend, user);

    if (isOneOptionalEmptyAndOneNotEmpty(friendshipOptional, reversedFriendshipOptional)) {
      var identifier = friendshipOptional.orElse(reversedFriendshipOptional.get()).getId();
      throw new FriendshipException(String.format(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN, identifier));
    }

    if (isBothOptionalEmpty(friendshipOptional, reversedFriendshipOptional)) {
      throw new FriendshipException("Пользователи не друзья");
    }

    if (isBothOptionalPresent(friendshipOptional, reversedFriendshipOptional)) {
      var friendship = friendshipOptional.get();
      var reversedFriendship = reversedFriendshipOptional.get();
      var friendshipStatus = friendship.getStatus();
      var reversedFriendshipStatus = reversedFriendship.getStatus();

      if (!(friendshipStatus.equals(FRIEND) && reversedFriendshipStatus.equals(FRIEND))) {
        throw new FriendshipException("Пользователи не друзья");
      }

      friendship.setStatus(DECLINED);
      reversedFriendship.setStatus(SUBSCRIBED);

      friendshipRepository.saveAll(List.of(friendship, reversedFriendship));
    }

    return CommonResponseDTO.builder()
        .message("ok")
        .build();
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

  private boolean isOneOptionalEmptyAndOneNotEmpty(Optional<Friendship> optionalOne, Optional<Friendship> optionalTwo) {
    return optionalOne.isEmpty() && optionalTwo.isPresent() || optionalOne.isPresent() && optionalTwo.isEmpty();
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

  @SuppressWarnings({"Duplicates", "OptionalGetWithoutIsPresent", "java:S3655"})
  public CommonResponseDTO<MessageResponseDTO> blockUser(Long id) {
    var target = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    var user = CurrentUserUtils.getCurrentUser();
    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, target);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(target, user);

    if (isOneOptionalEmptyAndOneNotEmpty(friendshipOptional, reversedFriendshipOptional)) {
      var identifier = friendshipOptional.orElse(reversedFriendshipOptional.get()).getId();
      throw new FriendshipException(String.format(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN, identifier));
    }

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
      throw new FriendshipException("Вы уже заблокировали пользователя");
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

  @SuppressWarnings({"Duplicates", "OptionalGetWithoutIsPresent", "java:S3655"})
  public CommonResponseDTO<MessageResponseDTO> unblockUser(Long id) {
    var target = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    var user = CurrentUserUtils.getCurrentUser();
    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, target);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(target, user);

    if (isOneOptionalEmptyAndOneNotEmpty(friendshipOptional, reversedFriendshipOptional)) {
      var identifier = friendshipOptional.orElse(reversedFriendshipOptional.get()).getId();
      throw new FriendshipException(String.format(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN, identifier));
    }

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

    throw new FriendshipException("Пользователь не заблокирован");
  }
}