package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.FriendsAdditionException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.IsFriendsDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StatusFriendDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.entity.Friendship;
import ru.skillbox.zerone.backend.model.entity.Recommendation;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus;
import ru.skillbox.zerone.backend.repository.FriendshipRepository;
import ru.skillbox.zerone.backend.repository.RecommendationRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.*;

import static ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus.*;

@Service
@RequiredArgsConstructor
public class FriendsService {
  private final FriendshipRepository friendshipRepository;
  private final RecommendationRepository recommendationRepository;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  @SuppressWarnings({"Duplicates", "OptionalGetWithoutIsPresent", "java:S3655"})
  public CommonResponseDTO<Object> addFriend(Long id) {
    var user = CurrentUserUtils.getCurrentUser();
    var friend = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

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

    throw new FriendsAdditionException(String.format("Неверная комбинация статусов: %s, %s", friendshipStatus, reversedFriendshipStatus));
  }

  private void checkNotAllowedStatusCombinationsAndThrowExceptionIfMatched(FriendshipStatus friendshipStatus, FriendshipStatus reversedFriendshipStatus) {
    if (friendshipStatus.equals(DEADLOCK) && reversedFriendshipStatus.equals(DEADLOCK)) {
      throw new FriendsAdditionException("У вас взаимная блокировка с пользователем");
    }

    if (friendshipStatus.equals(BLOCKED) && reversedFriendshipStatus.equals(WASBLOCKEDBY)) {
      throw new FriendsAdditionException("Вы заблокировали пользователя");
    }

    if (friendshipStatus.equals(WASBLOCKEDBY) && reversedFriendshipStatus.equals(BLOCKED)) {
      throw new FriendsAdditionException("Вы были заблокированы пользователем");
    }

    if (friendshipStatus.equals(FRIEND) && reversedFriendshipStatus.equals(FRIEND)) {
      throw new FriendsAdditionException("Пользователь уже в друзьях");
    }

    if (friendshipStatus.equals(SUBSCRIBED) && reversedFriendshipStatus.equals(REQUEST)) {
      throw new FriendsAdditionException("Вы уже отправили заявку в друзья");
    }
  }

  @Transactional
  @SuppressWarnings({"Duplicates", "OptionalGetWithoutIsPresent", "java:S3655"})
  public CommonResponseDTO<Object> removeFriend(Long id) {
    var user = CurrentUserUtils.getCurrentUser();
    var friend = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

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

      if (!(friendshipStatus.equals(FRIEND) && reversedFriendshipStatus.equals(FRIEND))) {
        throw new FriendsAdditionException("Пользователи не друзья");
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
    var pageable = PageRequest.of(offset, itemPerPage);

    if (name.isBlank()) {
      return friendshipRepository.findAllBySrcPersonAndStatus(user, status, pageable);
    } else {
      return friendshipRepository.findAllBySrcPersonAndStatusAndDstPersonNameLike(
          user, status, name, pageable);
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
    var user = CurrentUserUtils.getCurrentUser();
    Recommendation repositoryByUserId = createRecommendations(user);
    var recommendations = userRepository.findUsersById(repositoryByUserId.getRecommendedFriends());
    return CommonListResponseDTO.<UserDTO>builder()
        .total(recommendations.size())
        .offset(offset)
        .perPage(itemPerPage)
        .data(userMapper.usersToUserDTO(recommendations))
        .build();
  }

  public Recommendation createRecommendations(User user) {
    var currentFriends = recommendationRepository.findCurrentUserFriends(user.getId());
    var allUsers = new ArrayList<>(userRepository.findAllUsers().stream().limit(200).toList());
    var recommendedUsersByCity = userRepository.findUsersByCity(user.getCity());
    List<Long> recommendedUsersId = new ArrayList<>(recommendedUsersByCity);

    recommendedUsersId.removeAll(currentFriends);
    recommendedUsersId.remove(user.getId());
    if (recommendedUsersId.size() < 10) {
      allUsers.removeAll(recommendedUsersByCity);
      allUsers.removeAll(currentFriends);
      recommendedUsersId.addAll(allUsers);
    }

    var recommendations = recommendedUsersId.stream().distinct().limit(8).toList();

    return Recommendation.builder()
        .user(user)
        .recommendedFriends((recommendations))
        .build();
  }
}
