package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Tag;

import java.util.Optional;
@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Page <Tag> findTagsByPostTextContains (String tag, Pageable pageable);

  Optional <Tag> findByTag(String tag);
}
