package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostsDTO;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;

import static java.awt.SystemColor.text;

@Service
@AllArgsConstructor
public class PostService {
  private final PostRepository postRepository;


  public CommonResponseDTO<PostsDTO> createPost(int id, long publishData, PostRequestDTO postRequestDTO) {
    User user = CurrentUserUtils.getCurrentUser();

    Post post = new Post();
    post.setPostText(postRequestDTO.getPostText());
    post.setAuthor(user);
    post.setId(post.getId());
    post.setTime(LocalDateTime.now());
    post.setTitle(post.getTitle());
    post.setIsBlocked(false);

    Post savedPost = postRepository.save(post);

    PostsDTO postsDTO = new PostsDTO();
    postsDTO.setPostText(savedPost.getPostText());
    postsDTO.setAuthor(userMapper.userToUserDTO(user));

    CommonResponseDTO<PostsDTO> result = new CommonResponseDTO<>();
    result.setData(postsDTO);
    return result;


//    post.setPostText(postRequestDTO.getPostText());
//    post.setTitle(postRequestDTO.getTitle());
//    List<String> tags = postRequestDTO.getTags();
//    Post createdPost = postRepository.save(post);
////    Matcher images = pattern.matcher(postRequestDTO.getPostText());
//    CommonResponseDTO<PostsDTO> result = new CommonResponseDTO<>();
//    result.setTimestamp(LocalDateTime.now().toInstant(UTC));
//    result.setData(getPostsDTO(createdPost));
//    return result;

  }
//  private PostsDTO getPostsDTO(Post post, User user) {
//    PostsDTO postsDTO = new PostsDTO();
////    postsDTO.setPostText(post.getPostText());
////    postsDTO.setAuthor(setUserDTO(post.getUser()));
////    postsDTO.setId(post.getId());
////    postsDTO.setLikes(likes.size());
////    postsDTO.setTimestamp(post.getDatetime());
////    postsDTO.setTitle(post.getTitle());
////    postsDTO.setIsBlocked(post.isBlocked());
//
//    return postsDTO;
//  }

  public CommonListDTO<PostsDTO> getPostResponse(int offset, int itemPerPage, Page<Post> pageablePostList){
    CommonListDTO<PostsDTO> postResponse = new CommonListDTO<>();
    postResponse.setPerPage(itemPerPage);
    postResponse.setTimestamp(LocalDateTime.now().toInstant(UTC));
    postResponse.setOffset(offset);
    postResponse.setTotal((int) pageablePostList.getTotalElements());
    return getPostResponse (offset, itemPerPage, pageablePostList);
  }

  public CommonListDTO<PostsDTO> getFeeds(String text, int offset, int itemPerPege) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset, itemPerPege);
    return
  }
}
