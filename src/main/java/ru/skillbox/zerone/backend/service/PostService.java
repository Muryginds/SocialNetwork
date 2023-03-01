package ru.skillbox.zerone.backend.service;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.PostCreationExecption;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.UserAndAuthorEqualsException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.model.entity.*;
import ru.skillbox.zerone.backend.repository.*;
import ru.skillbox.zerone.backend.service.TagService;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.Principal;
import java.time.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final LikeRepository likeRepository;
  private final CommentService commentService;
  private final TagService tagService;
  private final FriendshipService friendshipService;
  private UserMapper userMapper;
  private final TagRepository tagRepository;
  private final UserRepository userRepository;
  private final FileRepository fileRepository;
  private final UserService userService;
  private static final Pattern pattern = Pattern.compile("<img\\s+[^>]*src=\"([^\"]*)\"[^>]*>");

  public CommonResponseDTO<PostsDTO> createPost(int id, long publishDate, PostRequestDTO postRequestDTO, Principal principal) throws PostCreationExecption{
      User user = findUser(principal.getName());
    if (user.getId() != id) throw new PostCreationExecption();
//User user = currentUserUtils.getCurrentUser();
    Post post = new Post();
    post.setPostText(postRequestDTO.getPostText());
    post.setTitle(postRequestDTO.getTitle());
    post.setAuthor(user);
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

    postsDTO.setComments(commentService.getPage4Comments(0,5,post, user));
    Set<Like> likes = likeRepository.findLikesByPost(post);
    postsDTO.setLikes(postsDTO.getLikes());
    postsDTO.setTimestamp(post.getTime());
    postsDTO.setId(post.getId());
    postsDTO.setTimestamp(post.getTime());
    postsDTO.setTitle(post.getTitle());
    postsDTO.setBlocked(post.getIsBlocked());
    postsDTO.setTags(new ArrayList<>());
//    if (post.getTags() != null) {
//      postsDTO.setTags(post.getTags.stream().map(Tag::getTag).collect(Collectors.toList()));
//    }
//    postsDTO.setMyLike(likes.stream()
//        .anyMatch(postLike -> postLike.getUser().equals(user)));
    if (LocalDateTime.now().isBefore(post.getTime())) {
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

  public CommonResponseDTO<PostsDTO> getPostById(int id, Principal principal) throws PostNotFoundException {

    Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    User user = userRepository.findUserByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(""));
    CommonResponseDTO<PostsDTO> dataResponse = new CommonResponseDTO<>();
    dataResponse.setTimestamp(LocalDateTime.now());
    dataResponse.setData(getPostsDTO(post, user));
    return dataResponse;
  }
  private Post findPost(int Id) throws PostNotFoundException {
    return postRepository.findById(Id)
        .orElseThrow(PostNotFoundException::new);
  }
  private CommonResponseDTO<PostsDTO> getPostDTOResponse(Post post, User user) {
    CommonResponseDTO<PostsDTO> postDataResponse = new CommonResponseDTO<>();
    postDataResponse.setTimestamp(LocalDateTime.now());
    postDataResponse.setData(getPostsDTO(post, user));

    return postDataResponse;
  }
  public CommonListDTO<PostsDTO> getAuthorWall(int id, int offset, int itemPerPage, Principal principal) {
    User user = findUser(principal.getName());
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList;
//   if (id == user.getId()) {
      pageablePostList = postRepository.findPostsByAuthorId(id, pageable);
//    } else if (!friendshipService.isBlockedBy(id, user.getId()) && !user.getIsDeleted()) {
//      pageablePostList = postRepository.findPostsByUserIdAndCurrentDate(id, pageable);
//    } else {
//      pageablePostList = Page.empty();
//    }

    return getPostResponse(offset, itemPerPage, pageablePostList, user);
  }

  public CommonListDTO<PostsDTO> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag, Principal principal) {
    User user = findUser(principal.getName());
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList;
//    Instant dateFrom = Instant.now();
//    LocalDateTime datetimeFrom = LocalDateTime.ofInstant (dateFrom, ZoneOffset.UTC);
    Instant datetimeTo = (dateTo == -1) ? ZonedDateTime.now().toInstant() : Instant.ofEpochSecond(dateTo);
    Instant datetimeFrom = (dateFrom == -1) ? ZonedDateTime.now().minusYears(1).toInstant() : Instant.ofEpochSecond(dateFrom);
    if (tag.equals("")) {
      pageablePostList = postRepository.findPostsByPostTextContainsAndAuthorLastNameAndUpdateTimeBetween(text, author,
         datetimeFrom, datetimeTo, pageable);
    } else {
////      List<Integer> tags = Arrays.stream(tag.split("_"))
////          .map(t -> tagRepository.findByTag(t).orElse(null))
////          .filter(Objects::nonNull).map(Tag::getId).collect(Collectors.toList());
      pageablePostList = postRepository.findPostsByPostTextContainsAndAuthorLastNameAndUpdateTimeBetween(text, author,
          datetimeFrom, datetimeTo, pageable);
////      pageablePostList = postRepository.findPostsByPostTextContainsAndAuthorAndUpdateTime(text, author, datetimeFrom,
////          datetimeTo, pageable, tags, tags.size());
    }
    return getPostResponse(offset, itemPerPage, pageablePostList, user);
  }

//  public static LocalDateTime getLocalDateTime(long milliseconds) {
//    return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
//  }
  public CommonResponseDTO<PostsDTO> deletePostById (int id, Principal principal) throws PostNotFoundException, UserAndAuthorEqualsException {
      User user = findUser(principal.getName());
      Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
      if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException();
      post.setIsDeleted(true);
//      post.setIsDeletedTime(LocalDateTime.now());
      postRepository.saveAndFlush(post);
      return getPostDTOResponse(post, user);
  }

  public CommonResponseDTO<PostsDTO> putPostIdRecover(int id, Principal principal) throws
      PostNotFoundException, UserAndAuthorEqualsException {
    User user = findUser(principal.getName());
    Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException();
    post.setIsDeleted(false);
    postRepository.saveAndFlush(post);
    return getPostDTOResponse(post, user);
  }
}
