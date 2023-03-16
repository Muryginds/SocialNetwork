package ru.skillbox.zerone.backend.service;


import com.tej.JooQDemo.jooq.sample.model.tables.records.UserRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;

import java.time.LocalDate;
import java.util.List;

import static com.tej.JooQDemo.jooq.sample.model.Tables.USER;
import static org.jooq.impl.DSL.*;


@Service
@RequiredArgsConstructor
public class SearchService {

  private final DSLContext dslContext;
  private final UserMapper userMapper;

  @Transactional
  public CommonListResponseDTO<UserDTO> searchUsers(String name, String lastName, String country, String city, Integer ageFrom, Integer ageTo, int itemPerPage) {


    Condition condition = createConditionForUsers(name, lastName, country, city, ageFrom, ageTo);

    List<UserRecord> userRecords = dslContext.selectFrom(USER)
        .where(condition).limit(itemPerPage).fetchInto(UserRecord.class);


    return CommonListResponseDTO.<UserDTO>builder()
        .total(userRecords.size())
        .perPage(itemPerPage)
        .offset(0)
        .data(userMapper.userRecordsToUserDTO(userRecords))
        .build();

    //Блок кода где я пытаюсь привести UserRecord к UserEntity

    /*List<User> users = dslContext.selectFrom(USER)
        .where(condition).limit(itemPerPage).fetchInto(User.class);


    return CommonListResponseDTO.<UserDTO>builder()
        .total(users.size())
        .perPage(itemPerPage)
        .offset(0)
        .data(userMapper.usersToUserDTO(users))
        .build();*/

  }

  private Condition createConditionForUsers(String name, String lastName, String country, String city, Integer ageFrom, Integer ageTo) {

    Condition condition = trueCondition();

    condition = condition.
        and((name != null) ? USER.FIRST_NAME.likeIgnoreCase("%" + name + "%") : noCondition()).
        and((lastName != null) ? USER.LAST_NAME.likeIgnoreCase("%" + lastName + "%") : noCondition()).
        and((country != null) ? USER.COUNTRY.likeIgnoreCase("%" + country + "%") : noCondition()).
        and((city != null) ? USER.CITY.likeIgnoreCase("%" + city + "%") : noCondition()).
        and((ageFrom != null) ? USER.BIRTH_DATE.lessOrEqual(LocalDate.now().minusYears(ageFrom)) : noCondition()).
        and((ageTo != null) ? USER.BIRTH_DATE.greaterOrEqual(LocalDate.now().minusYears(ageTo)) : noCondition());

    if (name == null &&
        lastName == null &&
        country == null &&
        city == null &&
        ageFrom == null &&
        ageTo == null) {

      condition = falseCondition();
    }

    return condition;
  }

  /*@Transactional
  public CommonListResponseDTO<PostDTO> searchPosts(String author, String pubDate, int itemPerPage) {
    List<PostDTO> postDTOS = new ArrayList<>();

    List<Post> postRecords = dslContext.select(POST.ID, POST.TITLE).from(POST).join(USER).on(USER.ID.eq(POST.AUTHOR_ID))
        .where(USER.FIRST_NAME.eq("Дмитрий")).limit(itemPerPage).fetchInto(Post.class);


    return CommonListResponseDTO.<PostDTO>builder()
        .total(postRecords.size())
        .perPage(itemPerPage)
        .offset(0)
        .data(postDTOS)
        .build();
  }*/
}