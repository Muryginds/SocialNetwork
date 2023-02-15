package ru.skillbox.zerone.backend.repository;

import org.hibernate.query.criteria.JpaFetchParent;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.FriendshipStatus;

public interface FriendshipStatusRepository extends JpaRepository<FriendshipStatus, Long> {
}
