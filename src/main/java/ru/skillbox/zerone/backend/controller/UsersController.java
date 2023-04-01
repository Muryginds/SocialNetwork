package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.service.FriendService;
import ru.skillbox.zerone.backend.service.SearchService;
import ru.skillbox.zerone.backend.service.UserService;

@RestController
@Validated
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {
  private final UserService userService;
  private final SearchService searchService;
  private final FriendService friendService;

  @GetMapping("/me")
  public CommonResponseDTO<UserDTO> getCurrentUser() {
    return userService.getCurrentUser();
  }

  @GetMapping("/{id}")
  public CommonResponseDTO<UserDTO> getById(@PathVariable long id) {
    return userService.getById(id);
  }

  @PutMapping("/me")
  public UserDTO editUserSettings(@RequestBody UserDTO updateUser) {
    return userService.editUserSettings(updateUser);
  }

  @GetMapping("/search")
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

  @PutMapping("/block/{id}")
  public CommonResponseDTO<MessageResponseDTO> blockUser(@PathVariable long id) {
    return friendService.blockUser(id);
  }

  @DeleteMapping("/block/{id}")
  public CommonResponseDTO<MessageResponseDTO> unblockUser(@PathVariable long id) {
    return friendService.unblockUser(id);
  }
}