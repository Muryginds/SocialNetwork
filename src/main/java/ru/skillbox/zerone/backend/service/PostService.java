package ru.skillbox.zerone.backend.service;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.PostCreationExecption;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.UserAndAuthorEqualsException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.model.entity.*;
import ru.skillbox.zerone.backend.repository.*;
import ru.skillbox.zerone.backend.service.TagService;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;


import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
//      pageablePostList = postRepository.findPostsByPersonIdAndCurrentDate(id, pageable);
//    } else {
//      pageablePostList = Page.empty();
//    }

    return getPostResponse(offset, itemPerPage, pageablePostList, user);
  }

  public CommonListDTO<PostsDTO> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag, Principal principal) {
    User user = findUser(principal.getName());
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Instant datetimeTo = (dateTo == -1) ? Instant.now() : Instant.ofEpochMilli(dateTo);
    Instant datetimeFrom = (dateFrom == -1) ? ZonedDateTime.now().minusYears(1).toInstant() : Instant.ofEpochMilli(dateFrom);
//    List<Integer> blockers = userRepository.findBlockersIds(user.getId());
//    blockers = !blockers.isEmpty() ? blockers : singletonList(-1);
    Page<Post> pageablePostList;
    if (tag.equals("")) {
      pageablePostList = postRepository.findPostsByPostTextContainsAndUpdateTime(text, author,
          datetimeFrom, datetimeTo, pageable);
    } else {
      List<Integer> tags = Arrays.stream(tag.split("_"))
          .map(t -> tagRepository.findByTag(t).orElse(null))
          .filter(Objects::nonNull).map(Tag::getId).collect(Collectors.toList());
      pageablePostList = postRepository.findPostsByPostTextContainsAndUpdateTime(text, author, datetimeFrom,
          datetimeTo, pageable, tags, tags.size());
    }

    return getPostResponse(offset, itemPerPage, pageablePostList, user);

  }

//  public CommonResponseDTO<PostsDTO> putPostById(int id, long publishDate, PostRequestDTO requestBody, Principal principal) throws PostNotFoundException, UserAndAuthorEqualsException {
//    User user = findUser(principal.getName());
//    Post post = findPost(id);
//    if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException();
//    post.setTitle(requestBody.getTitle());
//    post.setPostText(requestBody.getPostText());
//    List<String> tags = requestBody.getTags();
////    if (tags != null) {
////      post.setTags(tags.stream().map(s -> tagRepository.findByTag(s).orElse(null))
////          .filter(Objects::nonNull).collect(Collectors.toSet()));
////    }
////    post.setTime(LocalDateTime.now(publishDate == 0 ? System.currentTimeMillis() : publishDate));
//    post = postRepository.saveAndFlush(post);
//    Matcher images = pattern.matcher(requestBody.getPostText());
//    while (images.find()) {
////      PostFile file = fileRepository.findByUrl(images.group(1));
////      fileRepository.save(postFile.setPostId(post.getId()));
//    }
//    return getPostDTOResponse(post, user);
//  }
  public CommonResponseDTO<PostsDTO> deletePostById (int id, Principal principal) throws PostNotFoundException, UserAndAuthorEqualsException {
      User user = findUser(principal.getName());
      Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
      if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException();
      post.setIsDeleted(true);
//      post.setDeletedTimestamp(LocalDateTime.now());
      postRepository.saveAndFlush(post);
      return getPostDTOResponse(post, user);
  }

//  public CommonResponseDTO<PostsDTO> putPostIdRecover(int id, Principal principal) throws
//      PostNotFoundException, UserAndAuthorEqualsException {
//    User user = findUser(principal.getName());
//    Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
//    if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException();
//    post.setIsDeleted(false);
//    postRepository.saveAndFlush(post);
//    return getPostDTOResponse(post, user);
//  }

//
// Post savedPost = postRepository.save(post);
//    post.setPostText(postRequestDTO.getPostText());
//    post.setAuthor(user);
//    post.setId(post.getId());
//    post.setTime(LocalDateTime.now());
//    post.setTitle(postRequestDTO.getTitle());
//    post.setIsBlocked(false);
//    PostsDTO postsDTO = new PostsDTO();
//    postsDTO.setPostText(savedPost.getPostText());
//    postsDTO.setAuthor(userMapper.userToUserDTO(user));
//    postsDTO.setId(post.getId());
//    postsDTO.setLikes(postsDTO.getLikes());
//    postsDTO.setTimestamp(post.getTime());
//    postsDTO.setTitle(post.getTitle());
//    postsDTO.setBlocked(post.getIsBlocked());
//        List<String> tags = postRequestDTO.getTags();
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
//      CommonResponseDTO<PostsDTO> result = new CommonResponseDTO<>();
//    result.setTimestamp(LocalDateTime.now());
//       result.setData(postsDTO);
//    return result;
}
