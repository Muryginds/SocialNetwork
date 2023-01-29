package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
