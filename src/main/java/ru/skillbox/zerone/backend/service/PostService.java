package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.ListPostsDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostsDTO;
import ru.skillbox.zerone.backend.repository.PostRepository;

@Service
@AllArgsConstructor
public class PostService {
  private final PostRepository postRepository;


  public ListPostsDTO <PostsDTO> createPost(int id, long publishData, PostRequestDTO postRequestDTO) {

    return null;
  }

  public ListPostsDTO<PostsDTO> getFeeds(String text, int offset, int itemPerPege) {
    return null;
  }
}
