package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.LikeType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
  Set<Like> findLikesByPost(Post post);

  Integer countByPost(Post post);

  Integer countByComment(Comment comment);

  Set<Like> findLikesByCommentIdAndType(long id, LikeType typeLike);

  @Query
      ("""
          SELECT L.user.id FROM Like L WHERE CASE :type WHEN 'POST' THEN (L.post.id=:entityId) ELSE (L.comment.id=:entityId) END  
          """)
  List<Long> findLikers(long entityId, String type);

  Optional<Like> findByUserAndPost(User user, Post post);

  Optional<Like> findByUserAndComment(User user, Comment comment);


}
