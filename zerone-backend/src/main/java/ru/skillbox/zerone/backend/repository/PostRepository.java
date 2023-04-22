package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Post;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  @Query("""
      SELECT p FROM Post p where p.author.id = :id ORDER BY p.updateTime DESC
      """)
  Page<Post> getPostsForUsersWall(long id, Pageable pageable);

  @Query("""
      SELECT p FROM Post p where p.author.id <> :id
      and (p.updateTime > p.time or p.updateTime = p.time)
      and p.isDeleted = false ORDER BY p.updateTime DESC
      """)
  Page<Post> getPostsForFeeds(long id, Pageable pageable);
}
