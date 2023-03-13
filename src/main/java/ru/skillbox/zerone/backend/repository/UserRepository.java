package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);

  Optional<User> findUserByEmail(String email);

  @Query(value = """
      SELECT u FROM User u
      WHERE u.id IN :ids
      """)
  List<User> findUsersById(List<Long> ids);

  @Query(value = """
      SELECT u.id FROM User u
      WHERE u.city = :city
      AND u.isBlocked = false
      """)
  List<Long> findUsersByCity(String city);

  @Query(value = """
      SELECT u.id FROM User u
      WHERE u.isBlocked = false
      """)
  List<Long> findAllUsers();
}