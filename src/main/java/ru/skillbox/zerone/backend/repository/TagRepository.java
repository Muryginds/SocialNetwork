package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
