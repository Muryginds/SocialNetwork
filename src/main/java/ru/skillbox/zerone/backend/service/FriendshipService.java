package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.entity.Friendship;
import ru.skillbox.zerone.backend.repository.FriendshipRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendshipService {
  private final FriendshipRepository friendshipRepository;



//  public boolean isBlockedBy(int blocker, int blocked) {
//    Optional<Friendship> optional = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(blocker, blocked);
//    return isBlockedBy(blocker, blocked);
//  }

  public boolean isBlockedBy(int id, int id1) {
    Optional<Friendship> optional = friendshipRepository.findFriendshipBySrcPersonAndDstPerson(id, id1);
    return isBlockedBy(id, id1);
  }
}
