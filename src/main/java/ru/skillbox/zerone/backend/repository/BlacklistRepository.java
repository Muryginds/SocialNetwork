package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.BlacklistToken;

import java.util.Date;
import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<BlacklistToken, Long> {
  boolean existsByToken(String token);
  void deleteByExpiredLessThan(Date date);
}
