package ru.skillbox.zerone.backend.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.repository.LikeRepository;

import java.time.LocalDateTime;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class LikeTest extends AbstractIntegrationTest {
  @Autowired
  private  LikeRepository likeRepository;

  @Test
  void testFindLikesByPost() {

    Post post = new Post();
    post.setId(1L);
    Like like = new Like();
    like.setPost(post);
    likeRepository.save(like);


    Set <Like> likes = likeRepository.findLikesByPost(post);


    assertTrue(likes.contains(like));
  }
  @Test
  void testCurrentLocalDateTime() {
    LocalDateTime localDateTime = LocalDateTime.now();
    Like like = new Like();
    assertTrue(localDateTime.isBefore(like.getTime()) || localDateTime.isEqual(like.getTime()));
  }
  @Test
  void testLikeCreating() {
    long id = 1L;
    Like like = new Like();
    like.setId(id);
    assertTrue(likeRepository.existsById(id));
  }
}

