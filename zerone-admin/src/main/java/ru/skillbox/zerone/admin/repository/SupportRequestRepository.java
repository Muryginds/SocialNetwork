package ru.skillbox.zerone.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.admin.model.entity.SupportRequest;
import ru.skillbox.zerone.admin.model.enumerated.SupportRequestStatus;

import java.util.List;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
  List<SupportRequest> findAllByStatusOrderById(SupportRequestStatus status);
}
