package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Friendship;

import java.util.Optional;
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findFriendshipBySrcPersonAndDstPerson(int id, int id1);
//    Optional<Friendship> findFriendshipBySrcUserAndDstUser(int blocker, int blocked);

//  Optional<Friendship> findFriendshipBySrcUserAndDstUser(int id, int id1);
}
