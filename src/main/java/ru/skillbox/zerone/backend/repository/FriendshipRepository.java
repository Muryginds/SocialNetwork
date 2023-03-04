package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Friendship;
import ru.skillbox.zerone.backend.model.entity.User;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
  Optional<Friendship> findBySrcPersonAndDstPerson(User srcUser, User dstUser);
}
