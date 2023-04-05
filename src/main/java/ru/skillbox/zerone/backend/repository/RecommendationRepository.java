package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.zerone.backend.model.entity.Recommendation;
import ru.skillbox.zerone.backend.model.entity.User;

import java.util.Optional;


public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
  @Query(value = """
      SELECT r.dstPerson.id FROM Friendship r
      WHERE r.srcPerson.id = :id
      """)
  Optional<Long> findCurrentUserFriends(Long id);

  Recommendation findByUser(User user);
}
