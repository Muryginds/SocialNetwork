package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.zerone.backend.model.entity.Tag;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
