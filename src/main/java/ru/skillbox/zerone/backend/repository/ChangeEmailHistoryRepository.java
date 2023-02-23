package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.ChangeEmailHistory;

import java.util.Optional;


public interface ChangeEmailHistoryRepository extends JpaRepository<ChangeEmailHistory, Long> {

  Optional<ChangeEmailHistory> findFirstByEmailOldOrderByTimeDesc(String email);

}