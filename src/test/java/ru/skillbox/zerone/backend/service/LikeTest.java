package ru.skillbox.zerone.backend.service;


import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.repository.LikeRepository;

import java.time.LocalDateTime;

import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeTest {
  @Mock
  private  LikeRepository likeRepository;
  private PostService postService;

  @BeforeEach
  public void setUp() {
    likeRepository = Mockito.mock(LikeRepository.class);
  }

  @Test
  void testFindLikesByPost() {

    Post post = new Post();
    post.setId(1L);
    Like like = new Like();
    like.setPost(post);
    likeRepository.save(like);


    when(likeRepository.findLikesByPost(post)).thenReturn(Set.of(like));

    verify(this.likeRepository).findLikesByPost(post);
    assertEquals(like, like);
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

