package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.PostCreationExecption;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.UserAndAuthorEqualsException;
import ru.skillbox.zerone.backend.mapstruct.PostMapper;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostsDTO;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import ru.skillbox.zerone.backend.util.SerialaizTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final LikeRepository likeRepository;
  private final CommentService commentService;
  private final UserMapper userMapper;
  private final SerialaizTimeFormat serialaizTimeFormat;
  private final PostMapper postMapper;

  private static final Pattern pattern = Pattern.compile("<img\\s+[^>]*src=\"([^\"]*)\"[^>]*>");

  public CommonResponseDTO<PostsDTO> createPost(int id, long publishDate, PostRequestDTO postRequestDTO) {

    User user = CurrentUserUtils.getCurrentUser();
    if (user.getId() != id) throw new PostCreationExecption("Создан прекрасный мир.");

    Post post = new Post();
    post.setPostText(postRequestDTO.getPostText());
    post.setTitle(postRequestDTO.getTitle());
    post.setAuthor(user);
    if (publishDate == 0) {
      post.setTime(Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime());
    } else {
      post.setTime(Instant.ofEpochMilli(publishDate).atZone(ZoneId.systemDefault()).toLocalDateTime());
    }
    postRepository.save(post);
//    Matcher images = pattern.matcher(postRequestDTO.getPostText());
    CommonResponseDTO<PostsDTO> result = new CommonResponseDTO<>();
    result.setTimestamp(LocalDateTime.now());
    result.setData(getPostsDTO(post, user));
    return result;
}

  private PostsDTO getPostsDTO(Post post, User user) {

    PostsDTO postsDTO = postMapper.postToPostsDTO(post);

    postsDTO.setAuthor(userMapper.userToUserDTO(user));
    postsDTO.setComments(commentService.getPage4Comments(0,5,post, user));
    Set<Like> likes = likeRepository.findLikesByPost(post);
    postsDTO.setLikes(likes.size());

    postsDTO.setTags(new ArrayList<>());

    if (LocalDateTime.now().isBefore(post.getTime())) {
      postsDTO.setType("QUEUED");
    } else postsDTO.setType("POSTED");
    return postsDTO;
  }

  public CommonListResponseDTO<PostsDTO> getPostResponse(int offset, int itemPerPage, Page<Post> pageablePostList, User user){

        return CommonListResponseDTO.<PostsDTO>builder()
        .total((int)pageablePostList.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .timestamp(LocalDateTime.now())
        .data(getPostforResponse(pageablePostList.toList(), user))
        .build();

 }

  public List<PostsDTO> getPostforResponse(List<Post> posts, User user) {
    List<PostsDTO> postDataList = new ArrayList<>();
    posts.forEach(post -> {
      PostsDTO postData = getPostsDTO(post, user);
      postDataList.add(postData);
    });
    return postDataList;
  }

  public CommonListResponseDTO<PostsDTO> getFeeds(String text, int offset, int itemPerPage) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset, itemPerPage);
    Page<Post> pageablePostList = postRepository.findPostsByPostTextContains(text, pageable);
    return getPostResponse(offset, itemPerPage, pageablePostList, user);

  }

  public CommonResponseDTO<PostsDTO> getPostById(long id) throws PostNotFoundException, UserAndAuthorEqualsException {

    Post post = postRepository.findById(id).orElseThrow();
    User user = CurrentUserUtils.getCurrentUser();
    CommonResponseDTO<PostsDTO> dataResponse = new CommonResponseDTO<>();
    dataResponse.setTimestamp(LocalDateTime.now());
    dataResponse.setData(getPostsDTO(post, user));
    return dataResponse;
  }
  private Post findPost(long Id) throws PostNotFoundException {
    return postRepository.findById(Id).orElseThrow();
  }
  private CommonResponseDTO<PostsDTO> getPostDTOResponse(Post post, User user) {
    CommonResponseDTO<PostsDTO> postDataResponse = new CommonResponseDTO<>();
    postDataResponse.setTimestamp(LocalDateTime.now());
    postDataResponse.setData(getPostsDTO(post, user));

    return postDataResponse;
  }
  public CommonListResponseDTO<PostsDTO> getAuthorWall(int id, int offset, int itemPerPage) {


    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList;
    if (id == user.getId()) {
      pageablePostList = postRepository.findPostsByAuthorId(id, pageable);
    }else{
      pageablePostList = Page.empty();
    }

    return getPostResponse(offset, itemPerPage, pageablePostList, user);
  }

  public CommonListResponseDTO<PostsDTO> getPosts(String text,  long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList;
    LocalDateTime datetimeFrom = Instant.ofEpochMilli(dateFrom).atZone(ZoneId.systemDefault()).toLocalDateTime();
    LocalDateTime datetimeTo = Instant.ofEpochMilli(dateTo).atZone(ZoneId.systemDefault()).toLocalDateTime();
    if (tag.equals("")) {
      pageablePostList = postRepository.findPostsByPostTextContainsAndAuthorLastNameAndUpdateTimeBetween(text, author,
          datetimeFrom,datetimeTo, pageable);
    } else {

      pageablePostList = postRepository.findPostsByPostTextContainsAndAuthorLastNameAndUpdateTimeBetween(text, author,
          datetimeFrom,datetimeTo, pageable);
    }
    return getPostResponse(offset, itemPerPage, pageablePostList, user);
  }

  public CommonResponseDTO<PostsDTO> deletePostById (long id) throws PostNotFoundException, UserAndAuthorEqualsException {
      User user = CurrentUserUtils.getCurrentUser();
      Post post = postRepository.findById(id).orElseThrow();
      if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException("Людей вообще нет!");
      post.setIsDeleted(true);
//      post.setIsDeletedTime(LocalDateTime.now());
      postRepository.saveAndFlush(post);
      return getPostDTOResponse(post, user);
  }

  public CommonResponseDTO<PostsDTO> putPostIdRecover(long id) throws PostNotFoundException, UserAndAuthorEqualsException {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow();
    if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException("Людей вообще нет!");
    post.setIsDeleted(false);
    postRepository.saveAndFlush(post);
    return getPostDTOResponse(post, user);
  }

  public CommonResponseDTO<PostsDTO> putPostById(int id, Long publishDate, PostRequestDTO requestBody) throws PostNotFoundException, UserAndAuthorEqualsException {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = findPost(id);
    if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException("Людей вообще нет!");
    post.setTitle(requestBody.getTitle());
    post.setPostText(requestBody.getPostText());
    List<String> tags = requestBody.getTags();
    post.setTime(Instant.ofEpochMilli(publishDate == 0 ? System.currentTimeMillis() : publishDate).atZone(ZoneId.systemDefault()).toLocalDateTime());
    post = postRepository.saveAndFlush(post);
    Matcher images = pattern.matcher(requestBody.getPostText());
    return getPostDTOResponse(post, user);
  }
}
