package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
