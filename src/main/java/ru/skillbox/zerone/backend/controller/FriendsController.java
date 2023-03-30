package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.IsFriendsDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.service.FriendService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FriendsController {
  private final FriendService friendService;

  @PostMapping("/friends/{id}")
  public CommonResponseDTO<MessageResponseDTO> addFriend(@PathVariable @Min(1) long id) {
    return friendService.addFriend(id);
  }

  @DeleteMapping("/friends/{id}")
  public CommonResponseDTO<MessageResponseDTO> removeFriend(@PathVariable @Min(1) long id) {
    return friendService.removeFriend(id);
  }

  @GetMapping("/friends")
  public CommonListResponseDTO<UserDTO> getFriendList(@RequestParam(name = "name", defaultValue = "") String name,
                                                      @RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                      @RequestParam(name = "itemPerPage", defaultValue = "20") @Min(0) int itemPerPage) {
    return friendService.getFriendList(name, offset, itemPerPage);
  }

  @GetMapping("/friends/request")
  public CommonListResponseDTO<UserDTO> getFriendRequestList(@RequestParam(name = "name", defaultValue = "") String name,
                                                             @RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                             @RequestParam(name = "itemPerPage", defaultValue = "20") @Min(0) int itemPerPage) {
    return friendService.getFriendRequestList(name, offset, itemPerPage);
  }

  @PostMapping("/is/friends")
  public CommonListResponseDTO<StatusFriendDTO> checkIsFriend(@Valid @RequestBody IsFriendsDTO isFriendsDTO) {
    return friendService.checkIsFriends(isFriendsDTO);
  }

  @GetMapping("/friends/recommendations")
  public CommonListResponseDTO<UserDTO> getRecommendations(@RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                           @RequestParam(name = "itemPerPage", defaultValue = "20") @Min(0) int itemPerPage) {
    return friendService.getRecommendations(offset, itemPerPage);
  }
}
