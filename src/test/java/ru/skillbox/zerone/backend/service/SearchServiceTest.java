package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Sql(scripts = "classpath:search-service-mock-data-insert.sql")
class SearchServiceTest extends AbstractIntegrationTest {

  @Autowired
  private SearchService searchService;
  private final PageRequest defaultPageable = PageRequest.of(0, 10);

  @Test
  void testSearchUsers_whenNoMatchFiltered_thenReturnEmptyPage() {
    var result = searchService.searchUsers(
        "name", "lastName", "country", "city", 18, 25, defaultPageable);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
  }

  @Test
  void testSearchUsers_whenFirstNameLastNameCountryAndCity_thenReturnExpectedUsers() {
    var firstName = "John";
    var lastName = "Doe";
    var country = "USA";
    var city = "New York";
    var ageFrom = 20;
    var ageTo = 30;
    var expectedPages = 1;
    var expectedUsersCount = 2;

    var result = searchService.searchUsers(firstName, lastName, country, city, ageFrom, ageTo, defaultPageable);

    assertNotNull(result);
    assertEquals(expectedPages, result.getTotalPages());
    assertEquals(expectedUsersCount, result.getContent().size());
  }

  @Test
  void testSearchUsers_whenNoParameters_thenReturnAllUsers() {
    var minUsersCount = 3;

    var response = searchService.searchUsers(null, null, null, null, null, null, defaultPageable);

    assertNotNull(response);
    assertTrue(minUsersCount <= response.getTotalElements());
  }

  @Test
  void testSearchUsers_whenFirstNameAndLastName_thenReturnExpectedUser() {
    var expectedUsersCount = 1;
    var firstName = "Jenya";
    var lastName = "Boy";
    var city = "Novosibirsk";

    var response = searchService.searchUsers(firstName, lastName, null, city, null, null, defaultPageable);
    var responseUser = response.getContent().get(0);

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());
    assertEquals(expectedUsersCount, response.getTotalElements());
    assertEquals(firstName, responseUser.getFirstName());
    assertEquals(lastName, responseUser.getLastName());
    assertEquals(city, responseUser.getCity());
  }

  @Test
  void testSearchPosts_whenTextAuthorAndTag_thenReturnExpectedPosts() {
    var text = "Some text";
    var author = "John Doe";
    var tag = "tag1";
    var pubDate = LocalDateTime.of(2023, 1, 1, 0, 0)
        .atZone(ZoneId.systemDefault()).toEpochSecond();
    var expectedPages = 1;
    var expectedPostsCount = 2;

    var result = searchService.searchPosts(text, author, tag, pubDate, defaultPageable);

    assertNotNull(result);
    assertEquals(expectedPages, result.getTotalPages());
    assertEquals(expectedPostsCount, result.getContent().size());
  }

  @Test
  void testSearchPosts_whenText_thenReturnExpectedPosts() {
    var text = "Some text";
    var expectedPages = 1;
    var expectedPostsCount = 3;

    var result = searchService.searchPosts(text, null, null, null, defaultPageable);

    assertNotNull(result);
    assertEquals(expectedPages, result.getTotalPages());
    assertEquals(expectedPostsCount, result.getTotalElements());
  }

  @Test
  void testSearchPosts_whenTextAndName_thenReturnExpectedPost() {
    var text = "Test3 post";
    var name = "Jenya";
    var expectedPages = 1;
    var expectedPostsCount = 1;

    var result = searchService.searchPosts(text, name, null, null, defaultPageable);
    var post = result.getContent().get(0);

    assertNotNull(result);
    assertEquals(expectedPages, result.getTotalPages());
    assertFalse(result.getContent().isEmpty());
    assertEquals(expectedPostsCount, result.getTotalElements());
    assertTrue(post.getPostText().toLowerCase().contains(text.toLowerCase()));
    assertTrue(post.getAuthor().getFirstName().toLowerCase().contains(name.toLowerCase()));
  }

  @Test
  void testSearchPosts_whenNoQuery_thenReturnAllPosts() {
    var minPostsCount = 3;

    var result = searchService.searchPosts(null, null, null, null, defaultPageable);

    assertNotNull(result);
    assertTrue(minPostsCount <= result.getTotalElements());
  }

  @Test
  void searchPosts_whenNoMatches_thenReturnEmptyPage() {
    var result = searchService.searchPosts("WRONG TEXT", "WRONG AUTHOR", "test_java", null, defaultPageable);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
  }
}