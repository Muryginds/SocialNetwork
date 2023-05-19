package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.PostCreationException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.UserAndAuthorNotEqualsException;
import ru.skillbox.zerone.backend.mapstruct.PostMapper;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.PostFile;
import ru.skillbox.zerone.backend.model.entity.Tag;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.PostFileRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("java:S5852")
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final TagService tagService;
  private final PostFileRepository postFileRepository;
  private final SearchService searchService;
  private final PostMapper postMapper;
  private final NotificationService notificationService;

  private static final Pattern pattern = Pattern.compile("<img\\s+[^>]*src=\"([^\"]*)\"[^>]*>");

  @Transactional
  public CommonResponseDTO<PostDTO> createPost(long userId, LocalDateTime publishDate, PostRequestDTO postRequestDTO) {

    User user = CurrentUserUtils.getCurrentUser();

    if (user.getId() != userId) {
      throw new PostCreationException("Попытка публикации неизвестным пользователем");
    }

    Post post = Post.builder()
        .postText(postRequestDTO.getPostText())
        .title(postRequestDTO.getTitle())
        .author(user)
        .tags(getTagsByPost(postRequestDTO.getTags()))
        .time(publishDate.isBefore(LocalDateTime.now()) ? LocalDateTime.now() : publishDate)
        .build();
    Matcher images = pattern.matcher(postRequestDTO.getPostText());
    while (images.find()) {
      PostFile file = postFileRepository.findByPath(images.group(1));
      postFileRepository.save(file);
    }

    postRepository.save(post);
    notificationService.savePost(post);

    return commonResponseDTO(post);
  }

  @SuppressWarnings("java:S6204")
  private List<Tag> getTagsByPost(List<String> tagsFromRequest) {
    if (tagsFromRequest == null) {
      return List.of();
    }
    return tagsFromRequest
        .stream()
        .map(tagService::getTag)
        .collect(Collectors.toList());
  }

  public CommonListResponseDTO<PostDTO> getFeeds(int offset, int itemPerPage) {
    long myId = CurrentUserUtils.getCurrentUser().getId();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList = postRepository.getPostsForFeeds(myId, pageable);
    return commonListResponseDTO(offset, itemPerPage, pageablePostList);
  }

  public CommonResponseDTO<PostDTO> getPostById(long id) {
    Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    return commonResponseDTO(post);
  }


  public CommonListResponseDTO<PostDTO> getAuthorWall(long id, int offset, int itemPerPage) {
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList = postRepository.getPostsForUsersWall(id, pageable);
    return commonListResponseDTO(offset, itemPerPage, pageablePostList);
  }

  public CommonListResponseDTO<PostDTO> getPosts(String text, String author, String tag, Long dateFrom, int offset, int itemPerPage) {
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList = searchService.searchPosts(text, author, tag, dateFrom, pageable);
    return commonListResponseDTO(offset, itemPerPage, pageablePostList);
  }

  @Transactional
  public CommonResponseDTO<PostDTO> deletePostById(long id) {
    Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    checkPostAuthor(post);
    post.setIsDeleted(true);
    postRepository.saveAndFlush(post);
    return commonResponseDTO(post);
  }

  @Transactional
  public CommonResponseDTO<PostDTO> putPostIdRecover(long id) {
    Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    checkPostAuthor(post);
    post.setIsDeleted(false);
    postRepository.saveAndFlush(post);
    return commonResponseDTO(post);
  }

  @Transactional
  public CommonResponseDTO<PostDTO> putPostById(long id, LocalDateTime publishDate, PostRequestDTO requestBody) {
    Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    checkPostAuthor(post);
    post.setTitle(requestBody.getTitle());
    post.setPostText(requestBody.getPostText());
    post.setTags(getTagsByPost(requestBody.getTags()));
    post.setTime(publishDate.isBefore(LocalDateTime.now()) ? LocalDateTime.now() : publishDate);
    postRepository.saveAndFlush(post);
    return commonResponseDTO(post);
  }

  private CommonResponseDTO<PostDTO> commonResponseDTO(Post post) {
    return CommonResponseDTO.<PostDTO>builder()
        .timestamp(LocalDateTime.now())
        .data(postMapper.postToPostsDTO(post))
        .build();
  }

  private CommonListResponseDTO<PostDTO> commonListResponseDTO(int offset, int itemPerPage, Page<Post> pageablePostList) {
    return CommonListResponseDTO.<PostDTO>builder()
        .total(pageablePostList.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(postMapper.toDtoList(pageablePostList.toList()))
        .build();
  }

  private void checkPostAuthor(Post post) {
    User user = CurrentUserUtils.getCurrentUser();
    if (!user.getId().equals(post.getAuthor().getId())) {
      throw new UserAndAuthorNotEqualsException("Редактировать пост может только автор");
    }
  }
}