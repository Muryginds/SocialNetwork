package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import static com.tej.JooQDemo.jooq.sample.model.Tables.*;
import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.trueCondition;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final DSLContext dslContext;

  @Transactional
  @SuppressWarnings("java:S107")
  public CommonListResponseDTO<UserDTO> searchUsers(String name, String lastName, String country, String city, Integer ageFrom, Integer ageTo, int offset, int itemPerPage) {

    Condition condition = createConditionForUsers(name, lastName, country, city, ageFrom, ageTo);

    int usersCount = dslContext.fetchCount(USER, condition);

    List<UserDTO> users = dslContext.select().from(USER)
        .where(condition).offset(offset).limit(itemPerPage).fetchInto(UserDTO.class);

    return CommonListResponseDTO.<UserDTO>builder()
        .total(usersCount)
        .data(users)
        .build();
  }

  private Condition createConditionForUsers(String name, String lastName, String country, String city, Integer ageFrom, Integer ageTo) {

    return trueCondition()
        .and((name != null) ? USER.FIRST_NAME.containsIgnoreCase(name) : noCondition())
        .and((lastName != null) ? USER.LAST_NAME.containsIgnoreCase(lastName) : noCondition())
        .and((country != null) ? USER.COUNTRY.containsIgnoreCase(country) : noCondition())
        .and((city != null) ? USER.CITY.containsIgnoreCase(city) : noCondition())
        .and((ageFrom != null) ? USER.BIRTH_DATE.lessOrEqual(LocalDate.now().minusYears(ageFrom)) : noCondition())
        .and((ageTo != null) ? USER.BIRTH_DATE.greaterOrEqual(LocalDate.now().minusYears(ageTo)) : noCondition())
        .and(USER.IS_DELETED.eq(false))
        .and(USER.IS_APPROVED.eq(true));
  }

  @Transactional
  public Page<Post> searchPosts(String text, String author, String tag, long pubDate, Pageable pageable) {

    Condition condition = createConditionForPosts(text, author, tag, pubDate);

    long postsCount = dslContext.fetchCount(POST, condition);

    List<Post> postRecords = dslContext.select()
        .from(POST)
        .join(USER).on(USER.ID.eq(POST.AUTHOR_ID))
        .where(condition).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch(this::recordToPost);

    return new PageImpl<>(postRecords, pageable, postsCount);
  }

  private Condition createConditionForPosts(String text, String author, String tag, Long pubDate) {

    return trueCondition()
        .and((pubDate != null) ? POST.UPDATE_DATE.greaterOrEqual(getPubDate(pubDate)) : noCondition())
        .and((text != null) ? POST.POST_TEXT.containsIgnoreCase(text)
            .or(POST.TITLE.containsIgnoreCase(text)) : noCondition())
        .and((author != null) ? POST.AUTHOR_ID.in(dslContext.select(POST.AUTHOR_ID).from(POST)
            .join(USER).on(USER.ID.eq(POST.AUTHOR_ID))
            .where(conditionForAuthorName(author))) : noCondition())
        .and((tag != null) ? POST.ID.in(dslContext.select(POST.ID).from(POST)
            .join(POST_TO_TAG).on(POST.ID.eq(POST_TO_TAG.POST_ID))
            .join(TAG).on(TAG.ID.eq(POST_TO_TAG.TAG_ID))
            .where(TAG.TAG_.containsIgnoreCase(tag))) : noCondition())
        .and(POST.IS_DELETED.eq(false))
        .and(POST.IS_BLOCKED.eq(false))
        .and(POST.TIME.lessThan(LocalDateTime.now()));
  }

  private LocalDateTime getPubDate(long pubDate) {

    return LocalDateTime.ofInstant(Instant.ofEpochMilli(pubDate),
        TimeZone.getDefault().toZoneId());
  }

  private Condition conditionForAuthorName(String author) {

    String[] authorName = author.split("\\s");

    return (authorName.length > 1) ?
        (USER.FIRST_NAME.containsIgnoreCase(authorName[0])
            .and(USER.LAST_NAME.containsIgnoreCase(authorName[1])))
            .or(USER.FIRST_NAME.containsIgnoreCase(authorName[1])
                .and(USER.LAST_NAME.containsIgnoreCase(authorName[0])))
        :
        USER.FIRST_NAME.containsIgnoreCase(author)
            .or(USER.LAST_NAME.containsIgnoreCase(author));
  }

  private Post recordToPost(Record postRecord) {
    return Post.builder()
        .id(postRecord.get(POST.ID))
        .title(postRecord.get(POST.TITLE))
        .postText(postRecord.get(POST.POST_TEXT))
        .author(recordToUser(postRecord))
        .isBlocked(postRecord.get(POST.IS_BLOCKED))
        .isDeleted(postRecord.get(POST.IS_DELETED))
        .updateTime(postRecord.get(POST.UPDATE_DATE))
        .time(postRecord.get(POST.TIME))
        .build();
  }

  private User recordToUser(Record userRecord) {
    return User.builder()
        .id(userRecord.get(USER.ID))
        .firstName(userRecord.get(USER.FIRST_NAME))
        .lastName(userRecord.get(USER.LAST_NAME))
        .about(userRecord.get(USER.ABOUT))
        .email(userRecord.get(USER.EMAIL))
        .birthDate(userRecord.get(USER.BIRTH_DATE))
        .city(userRecord.get(USER.CITY))
        .country(userRecord.get(USER.COUNTRY))
        .isApproved(userRecord.get(USER.IS_APPROVED))
        .phone(userRecord.get(USER.PHONE))
        .photo(userRecord.get(USER.PHOTO))
        .regDate(userRecord.get(USER.REG_DATE))
        .status(userRecord.get(USER.STATUS))
        .lastOnlineTime(userRecord.get(USER.LAST_ONLINE_TIME))
        .isBlocked(userRecord.get(USER.IS_BLOCKED))
        .isDeleted(userRecord.get(USER.IS_DELETED))
        .build();
  }
}
