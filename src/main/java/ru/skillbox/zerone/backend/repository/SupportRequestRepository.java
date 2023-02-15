package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.SupportRequest;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
}
