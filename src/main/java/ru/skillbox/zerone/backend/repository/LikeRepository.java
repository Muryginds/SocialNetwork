package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;

import java.util.Set;
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Set<Like> findLikesByPost(Post post);

    Set<Like> findLikeByIdAndType(int id, String comment);
}
