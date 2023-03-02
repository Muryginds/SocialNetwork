package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.IsFriendsDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StatusFriendDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.service.FriendsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FriendsController {
  private final FriendsService friendsService;

  @PostMapping("/friends/{id}")
  public CommonResponseDTO<Object> addFriend(@PathVariable Long id) {
    return friendsService.addFriend(id);
  }

  @DeleteMapping("/friends/{id}")
  public CommonResponseDTO<Object> removeFriend(@PathVariable Long id) {
    return friendsService.removeFriend(id);
  }

  @GetMapping("/friends")
  public CommonListResponseDTO<UserDTO> getFriendList(@RequestParam(name = "name", defaultValue = "") String name,
                                                      @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                      @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage) {
    return friendsService.getFriendList(name, offset, itemPerPage);
  }

  @GetMapping("/friends/request")
  public CommonListResponseDTO<UserDTO> getFriendRequestList(@RequestParam(name = "name", defaultValue = "") String name,
                                                      @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                      @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage) {
    return friendsService.getFriendRequestList(name, offset, itemPerPage);
  }

  @PostMapping("/is/friends")
  public CommonListResponseDTO<StatusFriendDTO> checkIsFriend(@RequestBody IsFriendsDTO isFriendsDTO) {
    return friendsService.checkIsFriends(isFriendsDTO);
  }

  @GetMapping("/friends/recommendations")
  public CommonListResponseDTO<UserDTO> getRecommendations(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                              @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage) {
    return friendsService.getRecommendations(offset, itemPerPage);
  }
}
