package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
