package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.zerone.backend.model.entity.Recommendation;
import ru.skillbox.zerone.backend.model.entity.User;



public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
  @Query(value = """
      SELECT r.dstPerson.id FROM Friendship r
      WHERE r.srcPerson.id = :id
      """)
  Page<Long> findCurrentUserFriends(Long id, Pageable pageable);

  Recommendation findByUser(User user);
}
