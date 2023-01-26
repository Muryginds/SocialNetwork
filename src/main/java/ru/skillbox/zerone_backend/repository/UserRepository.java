package ru.skillbox.zerone_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone_backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
