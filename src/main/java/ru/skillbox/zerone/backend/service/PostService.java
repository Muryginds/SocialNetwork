package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.PostCreationException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.UserAndAuthorEqualsException;
import ru.skillbox.zerone.backend.mapstruct.PostMapper;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.PostType;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;


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
  private final PostMapper postMapper;

  private static final Pattern pattern = Pattern.compile("<img\\s+[^>]*src=\"([^\"]*)\"[^>]*>");

  public CommonResponseDTO<PostDTO> createPost(int id, long publishDate, PostRequestDTO postRequestDTO) {

    User user = CurrentUserUtils.getCurrentUser();
    if (user.getId() != id) throw new PostCreationException("Создан прекрасный мир.");

    Post post = new Post();
    post.setPostText(postRequestDTO.getPostText());
    post.setTitle(postRequestDTO.getTitle());
    post.setAuthor(user);
    if (publishDate == 0) {
      post.setTime(LocalDateTime.now());
    } else {
      post.setTime(Instant.ofEpochMilli(publishDate).atZone(ZoneId.systemDefault()).toLocalDateTime());
    }
    postRepository.save(post);
//    Matcher images = pattern.matcher(postRequestDTO.getPostText());
    CommonResponseDTO<PostDTO> result = new CommonResponseDTO<>();
    result.setTimestamp(LocalDateTime.now());
    result.setData(getPostsDTO(post, user));
    return result;
  }

  private PostDTO getPostsDTO(Post post, User user) {

    PostDTO postDTO = postMapper.postToPostsDTO(post);

    postDTO.setAuthor(userMapper.userToUserDTO(user));
    postDTO.setComments(commentService.getPage4Comments(0, 5, post, user));
    Set<Like> likes = likeRepository.findLikesByPost(post);
    postDTO.setLikes(likes.size());

    postDTO.setTags(new ArrayList<>());

    if (LocalDateTime.now().isBefore(post.getTime())) {
      postDTO.setType(PostType.QUEUED);
    } else postDTO.setType(PostType.POSTED);
    return postDTO;
  }

  public CommonListResponseDTO<PostDTO> getPostResponse(int offset, int itemPerPage, Page<Post> pageablePostList, User user) {

    return CommonListResponseDTO.<PostDTO>builder()
        .total(pageablePostList.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(getPost4Response(pageablePostList.toList(), user))
        .build();

  }

  public List<PostDTO> getPost4Response(List<Post> posts, User user) {
    List<PostDTO> postDataList = new ArrayList<>();
    posts.forEach(post -> {
      PostDTO postData = getPostsDTO(post, user);
      postDataList.add(postData);
    });
    return postDataList;
  }

  public CommonListResponseDTO<PostDTO> getFeeds(String text, int offset, int itemPerPage) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset, itemPerPage);
    Page<Post> pageablePostList = postRepository.findPostsByPostTextContains(text, pageable);
    return getPostResponse(offset, itemPerPage, pageablePostList, user);

  }

  public CommonResponseDTO<PostDTO> getPostById(long id) {

    Post post = postRepository.findById(id).orElseThrow();
    User user = CurrentUserUtils.getCurrentUser();
    CommonResponseDTO<PostDTO> dataResponse = new CommonResponseDTO<>();
    dataResponse.setTimestamp(LocalDateTime.now());
    dataResponse.setData(getPostsDTO(post, user));
    return dataResponse;
  }

  private Post findPost(long Id) throws PostNotFoundException {
    return postRepository.findById(Id).orElseThrow();
  }

  private CommonResponseDTO<PostDTO> getPostDTOResponse(Post post, User user) {
    CommonResponseDTO<PostDTO> postDataResponse = new CommonResponseDTO<>();
    postDataResponse.setTimestamp(LocalDateTime.now());
    postDataResponse.setData(getPostsDTO(post, user));

    return postDataResponse;
  }

  public CommonListResponseDTO<PostDTO> getAuthorWall(int id, int offset, int itemPerPage) {


    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList;
    if (id == user.getId()) {
      pageablePostList = postRepository.findPostsByAuthorId(id, pageable);
    } else {
      pageablePostList = Page.empty();
    }

    return getPostResponse(offset, itemPerPage, pageablePostList, user);
  }

  public CommonListResponseDTO<PostDTO> getPosts(String text, long dateFrom, long dateTo, int offset, int itemPerPage, String author, String tag) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList;
    LocalDateTime datetimeFrom = Instant.ofEpochMilli(dateFrom).atZone(ZoneId.systemDefault()).toLocalDateTime();
    LocalDateTime datetimeTo = Instant.ofEpochMilli(dateTo).atZone(ZoneId.systemDefault()).toLocalDateTime();
    if (tag.equals("")) {
      pageablePostList = postRepository.findPostsByPostTextContainsAndAuthorLastNameAndUpdateTimeBetween(text, author,
          datetimeFrom, datetimeTo, pageable);
    } else {

      pageablePostList = postRepository.findPostsByPostTextContainsAndAuthorLastNameAndUpdateTimeBetween(text, author,
          datetimeFrom, datetimeTo, pageable);
    }
    return getPostResponse(offset, itemPerPage, pageablePostList, user);
  }

  public CommonResponseDTO<PostDTO> deletePostById(long id) {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow();
    if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException("Людей вообще нет!");
    post.setIsDeleted(true);
//      post.setIsDeletedTime(LocalDateTime.now());
    postRepository.saveAndFlush(post);
    return getPostDTOResponse(post, user);
  }

  public CommonResponseDTO<PostDTO> putPostIdRecover(long id) {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow();
    if (!user.getId().equals(post.getAuthor().getId())) throw new UserAndAuthorEqualsException("Людей вообще нет!");
    post.setIsDeleted(false);
    postRepository.saveAndFlush(post);
    return getPostDTOResponse(post, user);
  }

  public CommonResponseDTO<PostDTO> putPostById(int id, Long publishDate, PostRequestDTO requestBody) {
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
