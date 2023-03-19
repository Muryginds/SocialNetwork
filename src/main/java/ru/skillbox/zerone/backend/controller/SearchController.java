package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.service.SearchService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SearchController {

  private final SearchService searchService;

  @GetMapping("/users/search")
  public CommonListResponseDTO<UserDTO> getUser(@RequestParam(name = "first_name", required = false) String name,
                                                @RequestParam(name = "last_name", required = false) String lastName,
                                                @RequestParam(name = "country", required = false) String country,
                                                @RequestParam(name = "city", required = false) String city,
                                                @RequestParam(name = "age_from", required = false) Integer ageFrom,
                                                @RequestParam(name = "age_to", required = false) Integer ageTo,
                                                @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage) {

    return searchService.searchUsers(name, lastName, country, city, ageFrom, ageTo, offset, itemPerPage);
  }

  @GetMapping("/post")
  public CommonListResponseDTO<PostDTO> getPost(@RequestParam(name = "author", required = false) String author,
                                                @RequestParam(name = "tag", required = false) String tag,
                                                @RequestParam(name = "date_from") long dateFrom,
                                                @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage) {

    return searchService.searchPosts(author, tag, dateFrom, offset, itemPerPage);
  }

}