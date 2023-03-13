package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.zerone.backend.model.entity.Recommendation;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
  List<Long> findByUserId(Long id);

  @Query(value = """
SELECT r.srcPerson.id AS recommendedUser FROM Friendship r
WHERE r.srcPerson.isBlocked = false
GROUP BY recommendedUser
ORDER BY COUNT(r.dstPerson) DESC

""") List<Long> findUsersWithLargestFriendList(String city);
}
