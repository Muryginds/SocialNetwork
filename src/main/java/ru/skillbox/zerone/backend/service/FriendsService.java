package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.entity.Friendship;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipCode;
import ru.skillbox.zerone.backend.repository.FriendshipRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsService {
  private final FriendshipRepository friendshipRepository;
  private final UserRepository userRepository;

  @Transactional
  public CommonResponseDTO<Object> addFriend(Long id) {
    var user = CurrentUserUtils.getCurrentUser();
    var friend = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s was not found", id)));
    List<Friendship> friendshipList = new ArrayList<>();

    var oldFriendshipOptional = friendshipRepository
        .findBySrcPersonAndDstPersonAndCodeIn(user, friend, List.of(FriendshipCode.REQUEST));

    if (oldFriendshipOptional.isPresent()) {
      var updatedFriendship = oldFriendshipOptional.get();
      updatedFriendship.setCode(FriendshipCode.FRIEND);
      updatedFriendship.setTime(LocalDateTime.now());
      friendshipList.add(updatedFriendship);
      var updatedReversedFriendship = friendshipRepository
          .findBySrcPersonAndDstPersonAndCodeIn(friend, user, List.of(FriendshipCode.SUBSCRIBED))
          //.orElseThrow(() -> new FriendshipNotFoundException());
          .orElseThrow(() -> new RuntimeException());
      updatedReversedFriendship.setCode(FriendshipCode.FRIEND);
      updatedReversedFriendship.setTime(LocalDateTime.now());
      friendshipList.add(updatedReversedFriendship);
    } else {
      var newFriendship = Friendship.builder()
          .srcPerson(user)
          .dstPerson(friend)
          .code(FriendshipCode.SUBSCRIBED)
          .build();
      friendshipList.add(newFriendship);
      var reversedFriendship = Friendship.builder()
          .srcPerson(friend)
          .dstPerson(user)
          .code(FriendshipCode.REQUEST)
          .build();
      friendshipList.add(reversedFriendship);
    }

    friendshipRepository.saveAll(friendshipList);
    return CommonResponseDTO.builder()
        .message("ok")
        .build();
  }

  public CommonResponseDTO<Object> removeFriend(Long id) {
    return null;
  }
}
