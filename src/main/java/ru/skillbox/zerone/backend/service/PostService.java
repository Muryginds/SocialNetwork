package ru.skillbox.zerone.backend.service;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.PostCreationExecption;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostsDTO;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.Tag;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.repository.TagRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.service.TagService;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;


import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final LikeRepository likeRepository;
  private final CommentService commentService;
  private final TagService tagService;
  private UserMapper userMapper;
    private final TagRepository tagRepository;
  private final UserRepository userRepository;

  public CommonResponseDTO<PostsDTO> createPost(int id, long publishDate, PostRequestDTO postRequestDTO, Principal principal) throws PostCreationExecption{
      User user = findUser(principal.getName());
    if (user.getId() != id) throw new PostCreationExecption();
    Post post = new Post();
//    post.setPostText(postRequestDTO.getPostText());
//    post.setAuthor(user);
//    post.setId(post.getId());
//    post.setTime(LocalDateTime.now());
//    post.setTitle(post.getTitle());
//    post.setIsBlocked(false);
//
    Post savedPost = postRepository.save(post);

//    PostsDTO postsDTO = new PostsDTO();
//    postsDTO.setPostText(savedPost.getPostText());
//    postsDTO.setAuthor(userMapper.userToUserDTO(user));
//    postsDTO.setId(post.getId());
//    postsDTO.setLikes(postsDTO.getLikes());
//    postsDTO.setTimestamp(post.getTime());
//    postsDTO.setTitle(post.getTitle());
//    postsDTO.setBlocked(post.getIsBlocked());

//
//    CommonResponseDTO<PostsDTO> result = new CommonResponseDTO<>();
//    result.setData(postsDTO);
//    return result;


    post.setPostText(postRequestDTO.getPostText());
    post.setTitle(postRequestDTO.getTitle());
    post.setAuthor(user);
//    List<String> tags = postRequestDTO.getTags();
//    if (tags != null) {
//      post.setTags(tags.stream()
//          .map(x -> tagRepository.findByTag(x).orElse(null))
//          .filter(Objects::nonNull).collect(Collectors.toSet()));
//    }
//    if (publishDate == 0) {
//      post.setTime(LocalDateTime.now());
//    } else {
//      post.setTime(LocalDateTime.from(Instant.ofEpochMilli(publishDate)));
//    }

    Post createdPost = postRepository.save(post);
//    Matcher images = pattern.matcher(postRequestDTO.getPostText());
    CommonResponseDTO<PostsDTO> result = new CommonResponseDTO<>();
    result.setTimestamp(LocalDateTime.now());
    result.setData(getPostsDTO(createdPost, user));
    return result;

  }
  private PostsDTO getPostsDTO(Post post, User user) {

    PostsDTO postsDTO = new PostsDTO();
    postsDTO.setPostText(post.getPostText());
    postsDTO.setAuthor(userMapper.userToUserDTO(user));
    postsDTO.setComments(commentService.getPage4PostComments(0, 5, post));
    Set<Like> likes = likeRepository.findLikesByPost(post);
    postsDTO.setLikes(postsDTO.getLikes());
    postsDTO.setTimestamp(post.getTime());
    postsDTO.setId(post.getId());
    postsDTO.setLikes(postsDTO.getLikes());
    postsDTO.setTimestamp(post.getTime());
    postsDTO.setTitle(post.getTitle());
    postsDTO.setBlocked(post.getIsBlocked());
//    if (post.getTags() != null) {
//      postsDTO.setTags(post.getTags().stream().map(Tag::getTag).collect(Collectors.toList()));
//    }
    postsDTO.setMyLike(likes.stream()
        .anyMatch(postLike -> postLike.getUser().equals(user)));
    if (Instant.now().isBefore(Instant.from(post.getTime()))) {
      postsDTO.setType("QUEUED");
    } else postsDTO.setType("POSTED");
    return postsDTO;
  }

  public CommonListDTO<PostsDTO> getPostResponse(int offset, int itemPerPage, Page<Post> pageablePostList, User user){
    CommonListDTO<PostsDTO> postResponse = new CommonListDTO<>();
    postResponse.setPerPage(itemPerPage);
    postResponse.setTimestamp(LocalDateTime.now());
    postResponse.setOffset(offset);
    postResponse.setTotal((int)pageablePostList.getTotalElements());
    postResponse.setData(getPostforResponse(pageablePostList.toList(), user));
    return postResponse;
  }

  public List<PostsDTO> getPostforResponse(List<Post> posts, User user) {
    List<PostsDTO> postDataList = new ArrayList<>();
    posts.forEach(post -> {
      PostsDTO postData = getPostsDTO(post, user);
      postDataList.add(postData);
    });
    return postDataList;
  }

  public CommonListDTO<PostsDTO> getFeeds(String text, int offset, int itemPerPage) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset, itemPerPage);
    Page<Post> pageablePostList = postRepository.findPostsByPostTextContains(text, pageable);
    return getPostResponse(offset, itemPerPage, pageablePostList, user);

  }
  private User findUser(String eMail) {
    return userRepository.findUserByEmail(eMail)
        .orElseThrow(() -> new UsernameNotFoundException(eMail));
  }
}
