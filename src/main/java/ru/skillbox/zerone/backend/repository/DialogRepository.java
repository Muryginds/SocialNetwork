package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Dialog;

public interface DialogRepository extends JpaRepository<Dialog, Long> {
}