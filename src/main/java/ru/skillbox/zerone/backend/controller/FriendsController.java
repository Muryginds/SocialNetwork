package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.controller.swaggerdoc.SwaggerFriendsController;
import ru.skillbox.zerone.backend.model.dto.request.IsFriendsDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.service.FriendService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FriendsController implements SwaggerFriendsController {
  private final FriendService friendService;

  @Override
  @PostMapping("/friends/{id}")
  public CommonResponseDTO<MessageResponseDTO> addFriend(@PathVariable long id) {
    return friendService.addFriend(id);
  }

  @Override
  @DeleteMapping("/friends/{id}")
  public CommonResponseDTO<MessageResponseDTO> removeFriend(@PathVariable long id) {
    return friendService.removeFriend(id);
  }

  @Override
  @GetMapping("/friends")
  public CommonListResponseDTO<UserDTO> getFriendList(@RequestParam(name = "name", defaultValue = "") String name,
                                                      @RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                      @RequestParam(name = "itemPerPage", defaultValue = "20") @Min(0) int itemPerPage) {
    return friendService.getFriendList(name, offset, itemPerPage);
  }

  @Override
  @GetMapping("/friends/request")
  public CommonListResponseDTO<UserDTO> getFriendRequestList(@RequestParam(name = "name", defaultValue = "") String name,
                                                             @RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                             @RequestParam(name = "itemPerPage", defaultValue = "20") @Min(0) int itemPerPage) {
    return friendService.getFriendRequestList(name, offset, itemPerPage);
  }

  @Override
  @PostMapping("/is/friends")
  public CommonListResponseDTO<StatusFriendDTO> checkIsFriend(@Valid @RequestBody IsFriendsDTO isFriendsDTO) {
    return friendService.checkIsFriends(isFriendsDTO);
  }

  @Override
  @GetMapping("/friends/recommendations")
  public CommonListResponseDTO<UserDTO> getRecommendations(@RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset,
                                                           @RequestParam(name = "itemPerPage", defaultValue = "20") @Min(0) int itemPerPage) {
    return friendService.getRecommendations(offset, itemPerPage);
  }
}
