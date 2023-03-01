package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.FriendsAdditionException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.entity.Friendship;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus;
import ru.skillbox.zerone.backend.repository.FriendshipRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendsService {
  private final FriendshipRepository friendshipRepository;
  private final UserRepository userRepository;

  @Transactional
  public CommonResponseDTO<Object> addFriend(Long id) {
    var user = CurrentUserUtils.getCurrentUser();
    var friend = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %s не найден", id)));

    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, friend);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(friend, user);

    if (isOneOptionalEmptyAndOneNotEmpty(friendshipOptional, reversedFriendshipOptional)) {
      var identifier = friendshipOptional.orElse(reversedFriendshipOptional.get()).getId();
      throw new FriendsAdditionException(String.format("Нет пары к записи с id: %s", identifier));
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

    if (friendshipStatus.equals(FriendshipStatus.DEADLOCK) && reversedFriendshipStatus.equals(FriendshipStatus.DEADLOCK)) {
      throw new FriendsAdditionException ("У вас взаимная блокировка с пользователем");
    }

    if (friendshipStatus.equals(FriendshipStatus.BLOCKED) && reversedFriendshipStatus.equals(FriendshipStatus.WASBLOCKEDBY)) {
      throw new FriendsAdditionException ("Вы заблокировали пользователя");
    }

    if (friendshipStatus.equals(FriendshipStatus.WASBLOCKEDBY) && reversedFriendshipStatus.equals(FriendshipStatus.BLOCKED)) {
      throw new FriendsAdditionException ("Вы были заблокированы пользователем");
    }

    if (friendshipStatus.equals(FriendshipStatus.FRIEND) && reversedFriendshipStatus.equals(FriendshipStatus.FRIEND)) {
      throw new FriendsAdditionException ("Пользователь уже в друзьях");
    }

    if (friendshipStatus.equals(FriendshipStatus.SUBSCRIBED) && reversedFriendshipStatus.equals(FriendshipStatus.REQUEST)) {
      throw new FriendsAdditionException ("Вы уже отправили заявку в друзья");
    }

    if (friendshipStatus.equals(FriendshipStatus.REQUEST) && reversedFriendshipStatus.equals(FriendshipStatus.SUBSCRIBED) ||
        friendshipStatus.equals(FriendshipStatus.DECLINED) && reversedFriendshipStatus.equals(FriendshipStatus.SUBSCRIBED)) {
      friendship.setStatus(FriendshipStatus.FRIEND);
      friendship.setTime(LocalDateTime.now());
      friendshipList.add(friendship);
      reversedFriendship.setStatus(FriendshipStatus.FRIEND);
      reversedFriendship.setTime(LocalDateTime.now());
      friendshipList.add(reversedFriendship);
      return friendshipList;
    }

    if (friendshipStatus.equals(FriendshipStatus.SUBSCRIBED) && reversedFriendshipStatus.equals(FriendshipStatus.DECLINED)) {
      reversedFriendship.setStatus(FriendshipStatus.REQUEST);
      reversedFriendship.setTime(LocalDateTime.now());
      friendshipList.add(reversedFriendship);
      return friendshipList;
    }

    throw new FriendsAdditionException(String.format("Неверная комбинация статусов: %s, %s", friendshipStatus, reversedFriendshipStatus));
  }

  @Transactional
  public CommonResponseDTO<Object> removeFriend(Long id) {
    var user = CurrentUserUtils.getCurrentUser();
    var friend = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %s не найден", id)));

    var friendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(user, friend);
    var reversedFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPerson(friend, user);

    if (isOneOptionalEmptyAndOneNotEmpty(friendshipOptional, reversedFriendshipOptional)) {
      var identifier = friendshipOptional.orElse(reversedFriendshipOptional.get()).getId();
      throw new FriendsAdditionException(String.format("Нет пары к записи с id: %s", identifier));
    }

    if (isBothOptionalEmpty(friendshipOptional, reversedFriendshipOptional)) {
      throw new FriendsAdditionException("Пользователи не друзья");
    }

    if (isBothOptionalPresent(friendshipOptional, reversedFriendshipOptional)) {
      var friendship = friendshipOptional.get();
      var reversedFriendship = reversedFriendshipOptional.get();
      var friendshipStatus = friendship.getStatus();
      var reversedFriendshipStatus = reversedFriendship.getStatus();

      if (!(friendshipStatus.equals(FriendshipStatus.FRIEND) && reversedFriendshipStatus.equals(FriendshipStatus.FRIEND))) {
        throw new FriendsAdditionException("Пользователи не друзья");
      }

      friendship.setStatus(FriendshipStatus.DECLINED);
      friendship.setTime(LocalDateTime.now());
      reversedFriendship.setStatus(FriendshipStatus.SUBSCRIBED);
      reversedFriendship.setTime(LocalDateTime.now());

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
        .status(FriendshipStatus.SUBSCRIBED)
        .build();
    var reversedFriendship = Friendship.builder()
        .srcPerson(friend)
        .dstPerson(user)
        .status(FriendshipStatus.REQUEST)
        .build();

    return List.of(newFriendship, reversedFriendship);
  }

  private boolean isBothOptionalPresent(Optional<?> optionalOne, Optional<?> optionalTwo) {
    return optionalOne.isPresent() && optionalTwo.isPresent();
  }

  private boolean isOneOptionalEmptyAndOneNotEmpty(Optional<?> optionalOne, Optional<?> optionalTwo) {
    return optionalOne.isEmpty() && optionalTwo.isPresent() || optionalOne.isPresent() && optionalTwo.isEmpty();
  }

  private boolean isBothOptionalEmpty(Optional<?> optionalOne, Optional<?> optionalTwo) {
    return optionalOne.isEmpty() && optionalTwo.isEmpty();
  }
}
