package ru.skillbox.zerone.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.skillbox.zerone.backend.mapstruct.PostMapper;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.testData.PostMockUtils;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest implements PostMockUtils {

  @Mock
  private PostRepository postRepository;

  @Mock
  private PostMapper postMapper;

  @Mock
  private UserMapper userMapper;

  @Mock
  private CommentService commentService;

  @Mock
  private LikeRepository likeRepository;

  private final MockedStatic<CurrentUserUtils> utilsMockedStatic = Mockito.mockStatic(CurrentUserUtils.class);

  @InjectMocks
  private PostService postService;

  private Post post;
  private User currentTestUser;

  @BeforeAll
  static void setStatic() {

  }

  @BeforeEach
  void setUp() {
    post = getTestPost();
    currentTestUser = new User().setId(1L);
  }

  @AfterEach
  void tearDown() {
    utilsMockedStatic.close();
  }


  @Test
  void createPostTest_whenSavePost_thenReturnsPostData() {

    UserDTO testUserDTO = UserDTO.builder().id(currentTestUser.getId()).firstName("Greg").lastName("Borovits").build();
    PostDTO postDTO = new PostDTO();
    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(currentTestUser);


    when(postMapper.postToPostsDTO(any(Post.class))).thenReturn(postDTO);
    when(userMapper.userToUserDTO(any(User.class))).thenReturn(testUserDTO);

    postDTO.setAuthor(userMapper.userToUserDTO(currentTestUser));
    postDTO.setPostText("TestText");

    int id = Math.toIntExact(currentTestUser.getId());
    CommonResponseDTO<PostDTO> response = postService.createPost(id, System.currentTimeMillis(), getPostRequest());

    assertNotNull(response);
    assertEquals(currentTestUser.getId(), response.getData().getAuthor().getId());
    assertEquals("TestText", response.getData().getPostText());
    assertNull(response.getError());
    verify(postRepository, Mockito.times(1))
        .save(Mockito.any(Post.class));
  }


  @Test
  void deletePostTest_whenDeletePost_thenSetsIsDeleted() {

    PostDTO postDTO = new PostDTO();
    UserDTO testUserDTO = UserDTO.builder().id(currentTestUser.getId()).firstName("Greg").lastName("Borovits").build();
    postDTO.setAuthor(testUserDTO);

    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(currentTestUser);

    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(postMapper.postToPostsDTO(any(Post.class))).thenReturn(postDTO);

    CommonResponseDTO<PostDTO> responseDTO = postService.deletePostById(post.getId());
    System.out.println(responseDTO);

    assertTrue(post.getIsDeleted());
    assertEquals(currentTestUser.getId(), responseDTO.getData().getAuthor().getId());
    assertNull(responseDTO.getError());
    verify(postRepository, Mockito.times(1))
        .saveAndFlush(Mockito.any(Post.class));
  }

  @Test
  void feedsTest_whenWeGetNews_thenReturnsPackOfFeeds() {

    Post testPost1 = Post.builder().postText("Post1").updateTime(LocalDateTime.of(2023, 3, 1, 22, 20, 3)).build();
    Post testPost2 = Post.builder().postText("Post2").updateTime(LocalDateTime.of(2023, 4, 2, 12, 10, 5)).build();

    List<Post> posts = new ArrayList<>();
    posts.add(testPost1);
    posts.add(testPost2);

    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(currentTestUser);

    when(postRepository.getPostsForFeeds(Mockito.eq(currentTestUser.getId()),
        any(Pageable.class))).thenReturn(new PageImpl<>(posts));

    CommonListResponseDTO<PostDTO> responseDTO = postService.getFeeds(0, 10);
    assertEquals(2, responseDTO.getData().size());
    verify(postRepository, Mockito.times(1))
        .getPostsForFeeds(Mockito.eq(currentTestUser.getId()), any(Pageable.class));
  }

  @Test
  void authorWallTest_whenWeGoToAuthorsWall_thenReturnsPackOfPosts() {

    Post testPost1 = Post.builder().postText("Post1").updateTime(LocalDateTime.of(2023, 3, 1, 22, 20, 3)).build();
    Post testPost2 = Post.builder().postText("Post2").updateTime(LocalDateTime.of(2023, 4, 2, 12, 10, 5)).build();

    List<Post> posts = new ArrayList<>();
    posts.add(testPost1);
    posts.add(testPost2);

    when(postRepository.getPostsForUsersWall(Mockito.eq(currentTestUser.getId()),
        any(Pageable.class))).thenReturn(new PageImpl<>(posts));

    CommonListResponseDTO<PostDTO> responseDTO = postService.getAuthorWall(currentTestUser.getId(), 0, 5);
    assertEquals(2, responseDTO.getData().size());
    verify(postRepository, Mockito.times(1))
        .getPostsForUsersWall(Mockito.eq(currentTestUser.getId()), any(Pageable.class));
  }

  @Test
  void postByIdTest_whenGetPostById_thenReturnsRequiredPostData() {

    PostDTO postDTO = new PostDTO();
    postDTO.setPostText("TestText");

    when(postMapper.postToPostsDTO(any(Post.class))).thenReturn(postDTO);
    when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

    CommonResponseDTO<PostDTO> responseDTO = postService.getPostById(1);

    assertEquals(post.getPostText(), responseDTO.getData().getPostText());
    verify(postRepository, Mockito.times(1))
        .findById(1L);
  }

  @Test
  void recoverPostTest_whenCallPutPostIdRecoverMethod_thenSetsIsDeletedFalse() {

    PostDTO postDTO = new PostDTO();
    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(currentTestUser);

    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    when(postMapper.postToPostsDTO(any(Post.class))).thenReturn(postDTO);
    post.setIsDeleted(true);

    CommonResponseDTO<PostDTO> responseDTO = postService.putPostIdRecover(post.getId());

    assertFalse(post.getIsDeleted());
    assertNull(responseDTO.getError());
    verify(postRepository, Mockito.times(1))
        .saveAndFlush(Mockito.any(Post.class));
  }

  @Test
  void editPostTest_whenCallPutPostByIdMethod_thenSetsNewFieldsFromRequest() {

    PostDTO postDTO = new PostDTO();

    Post testPost = Post.builder()
        .id(1L)
        .title("Создан тестовый пост")
        .postText("Тестовый текст")
        .author(currentTestUser)
        .time(LocalDateTime.of(2023, 5, 25, 6, 25, 44))
        .build();

    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(currentTestUser);
    when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
    when(postMapper.postToPostsDTO(testPost)).thenReturn(postDTO);

    CommonResponseDTO<PostDTO> responseDTO =
        postService.putPostById(testPost.getId(), System.currentTimeMillis(), new PostRequestDTO("Новый заголовок", "Новый текст", new ArrayList<>()));

    assertEquals("Новый заголовок", testPost.getTitle());
    assertEquals("Новый текст", testPost.getPostText());
    assertNull(responseDTO.getError());

  }

  private PostRequestDTO getPostRequest() {
    return PostRequestDTO.builder().postText(post.getPostText()).title(post.getTitle()).tags(Collections.emptyList())
        .build();
  }
}