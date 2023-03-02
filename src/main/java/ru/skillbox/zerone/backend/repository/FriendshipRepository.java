package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Friendship;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
  Optional<Friendship> findBySrcPersonAndDstPerson(User srcUser, User dstUser);
  Page<Friendship> findAllByDstPersonAndStatus(User srcUser, FriendshipStatus status, Pageable pageable);
  Page<Friendship> findAllByDstPersonAndStatusAndDstPersonFirstNameContainsIgnoreCaseOrDstPersonLastNameContainsIgnoreCase(
      User srcUser, FriendshipStatus status, String firstName, String lastName, Pageable pageable);
  List<Friendship> findAllBySrcPersonAndDstPersonIdIn(User user, List<Long> userIds);
}
