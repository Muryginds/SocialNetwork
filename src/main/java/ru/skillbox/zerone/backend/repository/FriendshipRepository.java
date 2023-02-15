package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
}
