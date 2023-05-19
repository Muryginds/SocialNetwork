package ru.skillbox.zerone.backend.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import ru.skillbox.zerone.backend.repository.TagRepository;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class TagControllerIT extends AbstractIntegrationTest {
  private static final String API_URL = "/api/v1/tags";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TagRepository tagRepository;

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void postTag_tagContainsOnlyNameAndTagNotExistsInDbAndUserIsAuthenticated_responseIsOkAndResponseContainsDataWithIdAndTag() throws Exception {
    var tag = RandomStringUtils.randomAlphanumeric(1, 15);
    mockMvc.perform(post(API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "tag": "{}"
                        }
                    """, tag)
                .getMessage()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.tag").value(tag))
        .andExpect(jsonPath("$.data.id").exists())
        .andExpect(jsonPath("$.data.id").isNumber())
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  void postTag_tagContainsNameAndUserIsNotAuthenticated_return403() throws Exception {
    var tag = RandomStringUtils.randomAlphanumeric(1, 15);
    mockMvc.perform(post(API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "tag": "{}"
                        }
                    """, tag)
                .getMessage()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.data.tag").doesNotExist())
        .andExpect(jsonPath("$.data.id").doesNotExist());
  }

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void postTag_tagContainsNameAndIdAndTagNotExistsInDbAndUserIsAuthenticated_responseIsOkAndResponseContainsDataWithIdAndTagAndIdIsNotEqualsSendValue() throws Exception {
    var tag = RandomStringUtils.randomAlphanumeric(1, 15);

    var id = new Random().nextInt(100)+1; //random.ints(1, 1,100);

    mockMvc.perform(post(API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "id": "{}",
                          "tag": "{}"
                        }
                    """, id, tag)
                .getMessage()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.tag").value(tag))
        .andExpect(jsonPath("$.data.id").exists())
        .andExpect(jsonPath("$.data.id").isNumber())
        .andExpect(jsonPath("$.data.id").value(Matchers.not(id)))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void postTag_tagContainsOnlyIdAndUserIsAuthenticated_getBadRequest() throws Exception {
    Random random = new Random();
    var id = random.ints(1, 1,100);

    mockMvc.perform(post(API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "id": "{}"
                        }
                    """, id)
                .getMessage()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.data").doesNotExist())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void postTag_tagContainsEmptyStringAndUserIsAuthenticated_getBadRequest() throws Exception {
    mockMvc.perform(post(API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "tag": ""
                    }
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.data").doesNotExist())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void postTag_requestWithEmptyBodyAndUserIsAuthenticated_getBadRequest() throws Exception {
    mockMvc.perform(post(API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.data").doesNotExist())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error").isNotEmpty())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  @Sql(scripts = "classpath:mock-tags-insert.sql")
  void postTag_tagAlreadyExistsInDb_statusIsOkAndReturnExistingTag() throws Exception {
    mockMvc.perform(post(API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "tag": "Test tag 1"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.tag").value("Test tag 1"))
        .andExpect(jsonPath("$.data.id").value(1_000_000))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void deleteTag_tagExistsInDbAndUserIsAuthenticated_responseIsOk() throws Exception {
    long tagId = 1_000_000;
    mockMvc.perform(delete(API_URL).param("id", String.valueOf(tagId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.message").value("OK"))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void deleteTag_tagNotExistsInDbAndUserIsAuthenticated_responseIsOk() throws Exception {
    Random random = new Random();
    long tagId = random.nextLong(1_000_000,100_000_000);

    mockMvc.perform(delete(API_URL).param("id", String.valueOf(tagId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.message").value("OK"))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @Sql(scripts = "classpath:mock-tags-insert.sql")
  void deleteTag_userIsNotAuthenticated_forbiddenAndTagExistsInDb() throws Exception {
    long tagId = 1_000_000;
    mockMvc.perform(delete(API_URL).param("id", String.valueOf(tagId)))
        .andExpect(status().isForbidden());
    assertTrue(tagRepository.findById(tagId).isPresent());
  }

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void deleteTag_requestDoesNotContainId_badRequest() throws Exception {
    mockMvc.perform(delete(API_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.message").value("OK"))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @Sql(scripts = "classpath:mock-tags-insert.sql")
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void getTags_getAllTagsWithDefaultOffsetAndLimit_return10Tags() throws Exception {
    mockMvc.perform(get(API_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(15))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.offset").value(0))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(10))
        .andExpect(jsonPath("$.data.[0].id").isNotEmpty())
        .andExpect(jsonPath("$.data.[0].id").isNumber())
        .andExpect(jsonPath("$.data.[0].tag").isString())
        .andExpect(jsonPath("$.data.[0].tag").isNotEmpty())
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @Sql(scripts = "classpath:mock-tags-insert.sql")
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void getTags_get3TagsWithoutOffset_return3Tags() throws Exception {
    mockMvc.perform(get(API_URL).param("itemPerPage", String.valueOf(3)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(15))
        .andExpect(jsonPath("$.perPage").value(3))
        .andExpect(jsonPath("$.offset").value(0))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(3))
        .andExpect(jsonPath("$.data.[0].id").isNotEmpty())
        .andExpect(jsonPath("$.data.[0].id").isNumber())
        .andExpect(jsonPath("$.data.[0].tag").isString())
        .andExpect(jsonPath("$.data.[0].tag").isNotEmpty())
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @Sql(scripts = "classpath:mock-tags-insert.sql")
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void getTags_get5TagsWithOffset5_return5Tags() throws Exception {
    mockMvc.perform(get(API_URL)
            .param("itemPerPage", String.valueOf(5))
            .param("offset", String.valueOf(5))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(15))
        .andExpect(jsonPath("$.perPage").value(5))
        .andExpect(jsonPath("$.offset").value(5))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(5))
        .andExpect(jsonPath("$.data.[0].id").isNotEmpty())
        .andExpect(jsonPath("$.data.[0].id").isNumber())
        .andExpect(jsonPath("$.data.[0].tag").isString())
        .andExpect(jsonPath("$.data.[0].tag").isNotEmpty())
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @Sql(scripts = "classpath:mock-tags-insert.sql")
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void getTags_getWithOffset5_return10Tags() throws Exception {
    mockMvc.perform(get(API_URL)
            .param("offset", String.valueOf(5))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(15))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.offset").value(5))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(10))
        .andExpect(jsonPath("$.data.[0].id").isNotEmpty())
        .andExpect(jsonPath("$.data.[0].id").isNumber())
        .andExpect(jsonPath("$.data.[0].tag").isString())
        .andExpect(jsonPath("$.data.[0].tag").isNotEmpty())
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @Sql(scripts = "classpath:mock-tags-insert.sql")
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void getTags_getWithOffset10000_returnEmptyData() throws Exception {
    mockMvc.perform(get(API_URL)
            .param("offset", String.valueOf(10_000))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(15))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.offset").value(10_000))
        .andExpect(jsonPath("$.data").isEmpty())
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  @Sql(scripts = "classpath:mock-tags-insert.sql")
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void getTags_getWithOffset10AndPerPage10_return5Tags() throws Exception {
    mockMvc.perform(get(API_URL)
            .param("itemPerPage", String.valueOf(10))
            .param("offset", String.valueOf(10))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(15))
        .andExpect(jsonPath("$.perPage").value(10))
        .andExpect(jsonPath("$.offset").value(10))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(5))
        .andExpect(jsonPath("$.data.[0].id").isNotEmpty())
        .andExpect(jsonPath("$.data.[0].id").isNumber())
        .andExpect(jsonPath("$.data.[0].tag").isString())
        .andExpect(jsonPath("$.data.[0].tag").isNotEmpty())
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }
}
