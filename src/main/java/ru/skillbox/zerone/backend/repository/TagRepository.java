package ru.skillbox.zerone.backend.repository;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

  Page<Tag> findByTag(String tag, Pageable pageable);

}
