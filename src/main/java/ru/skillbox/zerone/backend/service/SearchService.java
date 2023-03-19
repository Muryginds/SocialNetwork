package ru.skillbox.zerone.backend.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static com.tej.JooQDemo.jooq.sample.model.Tables.*;
import static org.jooq.impl.DSL.*;


@Service
@RequiredArgsConstructor
public class SearchService {

  private final DSLContext dslContext;
  private final PostService postService;


  @Transactional
  public CommonListResponseDTO<UserDTO> searchUsers(String name, String lastName, String country, String city, Integer ageFrom, Integer ageTo, int offset, int itemPerPage) {

    Condition condition = createConditionForUsers(name, lastName, country, city, ageFrom, ageTo);

    int count = dslContext.fetchCount(USER, condition);

    List<UserDTO> users = dslContext.select().from(USER)
        .where(condition).offset(offset).limit(itemPerPage).fetchInto(UserDTO.class);

    return CommonListResponseDTO.<UserDTO>builder()
        .total(count)
        .data(users)
        .build();

  }

  private Condition createConditionForUsers(String name, String lastName, String country, String city, Integer ageFrom, Integer ageTo) {

    return trueCondition().
        and((name != null) ? USER.FIRST_NAME.likeIgnoreCase("%" + name + "%") : noCondition()).
        and((lastName != null) ? USER.LAST_NAME.likeIgnoreCase("%" + lastName + "%") : noCondition()).
        and((country != null) ? USER.COUNTRY.likeIgnoreCase("%" + country + "%") : noCondition()).
        and((city != null) ? USER.CITY.likeIgnoreCase("%" + city + "%") : noCondition()).
        and((ageFrom != null) ? USER.BIRTH_DATE.lessOrEqual(LocalDate.now().minusYears(ageFrom)) : noCondition()).
        and((ageTo != null) ? USER.BIRTH_DATE.greaterOrEqual(LocalDate.now().minusYears(ageTo)) : noCondition());
  }

  @Transactional
  public CommonListResponseDTO<PostDTO> searchPosts(String author, String tag, long pubDate, int offset, int itemPerPage) {

    List<PostDTO> postDTOS = new ArrayList<>();

    Condition condition = createConditionForPosts(author, tag, pubDate);

    int count = dslContext.fetchCount(POST, condition);

    List<Post> postRecords = dslContext.select().from(POST)
        .where(condition).offset(offset).limit(itemPerPage).fetchInto(Post.class);

    postRecords.forEach(post -> {

      User user = Objects.requireNonNull(dslContext.select().from(USER)
          .join(POST).on(POST.AUTHOR_ID.eq(USER.ID)).where(POST.ID.eq(post.getId())).fetchAny()).into(User.class);

      PostDTO postDTO = postService.getPostsDTO(post, user);
      postDTOS.add(postDTO);
    });

    return CommonListResponseDTO.<PostDTO>builder()
        .total(count)
        .data(postDTOS)
        .build();
  }

  private Condition createConditionForPosts(String author, String tag, long pubDate) {

    return POST.UPDATE_DATE.greaterOrEqual(getPubDate(pubDate))
        .and((author != null) ? POST.AUTHOR_ID.in(select(POST.AUTHOR_ID).from(POST)
            .join(USER).on(USER.ID.eq(POST.AUTHOR_ID))
            .where(USER.FIRST_NAME.likeIgnoreCase(author)
                .or(USER.LAST_NAME.likeIgnoreCase(author)))) : noCondition())
        .and((tag != null) ? POST.ID.in(select(POST.ID).from(POST)
            .join(POST_TO_TAG).on(POST.ID.eq(POST_TO_TAG.POST_ID))
            .join(TAG).on(TAG.ID.eq(POST_TO_TAG.TAG_ID))
            .where(TAG.TAG_.likeIgnoreCase("%" + tag))) : noCondition());
  }

  private LocalDateTime getPubDate(long pubDate) {

    return LocalDateTime.ofInstant(Instant.ofEpochMilli(pubDate),
        TimeZone.getDefault().toZoneId());
  }
}