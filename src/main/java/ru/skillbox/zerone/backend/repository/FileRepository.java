package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.PostFile;

import java.io.File;
import java.util.List;
@Repository
public interface FileRepository extends JpaRepository <PostFile, Integer> {

//  PostFile findByUrl(String url);
//  List<PostFile> findByCommentId(@Param("commentId") Integer commentId);
//  List<PostFile> findByPostId(@Param("postId") Integer postId);

}
