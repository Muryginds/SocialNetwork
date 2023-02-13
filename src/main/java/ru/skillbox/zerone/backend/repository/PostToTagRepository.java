package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.PostToTag;
import ru.skillbox.zerone.backend.model.entity.Tag;

import java.util.List;

public interface PostToTagRepository extends JpaRepository<PostToTag, Long> {

  @Query("select t from Tag t, PostToTag pt where  pt.post = :post")
  List<Tag> findTagsByPost(@Param("post") Post post);

  @Query("select p from Post p, PostToTag pt where  pt.tag = :tag")
  List<Post> findPostsByTag(@Param("tag") Tag tag);
}
