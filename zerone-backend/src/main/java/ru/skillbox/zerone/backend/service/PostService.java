package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.PostCreationException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.TagNotFoundException;
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
import ru.skillbox.zerone.backend.repository.TagRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final SearchService searchService;
  private final PostMapper postMapper;
  private final TagRepository tagRepository;
  private final NotificationService notificationService;
  @SuppressWarnings("all")
  private static final Pattern pattern = Pattern.compile("<img\\s+[^>]*src=\"([^\"]*)\"[^>]*>");
  private final PostFileRepository postFileRepository;

  @Transactional
  public CommonResponseDTO<PostDTO> createPost(long id, long publishDate, PostRequestDTO postRequestDTO) {

    User user = CurrentUserUtils.getCurrentUser();

    if (user.getId() != id) {
      throw new PostCreationException("Попытка публикации неизвестным пользователем");
    }

    Post post = Post.builder()
        .postText(postRequestDTO.getPostText())
        .title(postRequestDTO.getTitle())
        .author(user)
        .tags(getTagsByPost(postRequestDTO.getTags()))
        .time(publishDate == 0 ? LocalDateTime.now()
            : Instant.ofEpochMilli(publishDate).atZone(ZoneId.systemDefault()).toLocalDateTime())
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

  private List<Tag> getTagsByPost(List<String> tagsFromRequest) {

    List<Tag> tags = new ArrayList<>();
    if (!tagsFromRequest.isEmpty()) {
      tagsFromRequest.forEach(tag -> {
        Tag tagFromRepo = tagRepository.findByName(tag).orElseThrow(() -> new TagNotFoundException("Тега не существует!"));
        tags.add(tagFromRepo);
      });
    }
    return tags;
  }

  private PostDTO getPostsDTO(Post post) {

    return postMapper.postToPostsDTO(post);
  }

  private List<PostDTO> getPost4Response(List<Post> posts) {

    List<PostDTO> postDataList = new ArrayList<>();
    posts.forEach(post -> postDataList.add(getPostsDTO(post)));

    return postDataList;
  }

  public CommonListResponseDTO<PostDTO> getFeeds(int offset, int itemPerPage) {

    long myId = CurrentUserUtils.getCurrentUser().getId();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Post> pageablePostList = postRepository.getPostsForFeeds(myId, pageable);

    return commonListResponseDTO(offset, itemPerPage, pageablePostList);
  }

  public CommonResponseDTO<PostDTO> getPostById(long id) {

    return commonResponseDTO(getPostFromRepo(id));
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

    Post post = getPostFromRepo(id);

    throwExceptionIfAuthorNotEqualsUser(post);

    post.setIsDeleted(true);
    postRepository.saveAndFlush(post);
    return commonResponseDTO(post);
  }

  @Transactional
  public CommonResponseDTO<PostDTO> putPostIdRecover(long id) {

    Post post = getPostFromRepo(id);

    throwExceptionIfAuthorNotEqualsUser(post);

    post.setIsDeleted(false);
    postRepository.saveAndFlush(post);
    return commonResponseDTO(post);
  }

  @Transactional
  public CommonResponseDTO<PostDTO> putPostById(long id, Long publishDate, PostRequestDTO requestBody) {

    Post post = getPostFromRepo(id);

    throwExceptionIfAuthorNotEqualsUser(post);

    post.setTitle(requestBody.getTitle());
    post.setPostText(requestBody.getPostText());
    post.setTags(getTagsByPost(requestBody.getTags()));
    post.setTime(Instant.ofEpochMilli(publishDate == 0 ? System.currentTimeMillis() : publishDate).atZone(ZoneId.systemDefault()).toLocalDateTime());
    postRepository.saveAndFlush(post);
    return commonResponseDTO(post);
  }

  private CommonResponseDTO<PostDTO> commonResponseDTO(Post post) {

    return CommonResponseDTO.<PostDTO>builder()
        .timestamp(LocalDateTime.now())
        .data(getPostsDTO(post))
        .build();
  }

  private CommonListResponseDTO<PostDTO> commonListResponseDTO(int offset, int itemPerPage, Page<Post> pageablePostList) {

    return CommonListResponseDTO.<PostDTO>builder()
        .total(pageablePostList.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(getPost4Response(pageablePostList.toList()))
        .build();
  }

  private Post getPostFromRepo(long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException("Пост с указанным id не найден"));
  }

  private void throwExceptionIfAuthorNotEqualsUser(Post post) {

    User user = CurrentUserUtils.getCurrentUser();
    if (!user.getId().equals(post.getAuthor().getId())) {
      throw new UserAndAuthorNotEqualsException("Попытка редактирования неизвестным пользователем");
    }
  }
}
