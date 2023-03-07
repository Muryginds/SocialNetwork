package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
  Page<Country> findAllByNameContains(String query, Pageable pageable);
}
