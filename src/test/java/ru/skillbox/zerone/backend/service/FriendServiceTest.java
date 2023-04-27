package ru.skillbox.zerone.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.skillbox.zerone.backend.exception.FriendshipException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.entity.Friendship;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus;
import ru.skillbox.zerone.backend.repository.FriendshipRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class FriendServiceTest {

  private AutoCloseable openMocks;
  private AutoCloseable utilsMockedStatic;
  @Mock
  private FriendshipRepository friendshipRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private NotificationService notificationService;

  private FriendService underTest;

  @BeforeEach
//  void setUp() {
//    openMocks = MockitoAnnotations.openMocks(this);
//    utilsMockedStatic = Mockito.mockStatic(CurrentUserUtils.class);
//    underTest = new FriendService(friendshipRepository, userRepository,
//        userMapper, notificationService);
//  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
    utilsMockedStatic.close();
  }

  @Test
  @SuppressWarnings("java:S5838")
  void itShouldAddFriendSuccessfully() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();

    long friendId = 5L;
    User friend = User.builder()
        .id(friendId).isBlocked(false).isDeleted(false).isApproved(true).build();

    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, friend)).willReturn(Optional.empty());
    given(friendshipRepository.findBySrcPersonAndDstPerson(friend, user)).willReturn(Optional.empty());

    // When

    CommonResponseDTO<MessageResponseDTO> responseDTO = underTest.addFriend(friendId);

    // Then

    ArgumentCaptor<List<Friendship>> friendshipArgumentCaptor = ArgumentCaptor.forClass(List.class);
    then(friendshipRepository).should().saveAll(friendshipArgumentCaptor.capture());
    List<Friendship> capture = friendshipArgumentCaptor.getValue();

    assertThat(capture.size()).isEqualTo(2);
    assertThat(capture.get(0).getStatus()).isEqualTo(FriendshipStatus.SUBSCRIBED);
    assertThat(capture.get(0).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(0).getDstPerson().getId()).isEqualTo(friendId);
    assertThat(capture.get(1).getStatus()).isEqualTo(FriendshipStatus.REQUEST);
    assertThat(capture.get(1).getSrcPerson().getId()).isEqualTo(friendId);
    assertThat(capture.get(1).getDstPerson().getId()).isEqualTo(userId);

    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");
  }

  @Test
  void itShouldThrowWhenPersonIsBlocked() {
    // Given
    long friendId = 5L;
    User friend = User.builder()
        .id(friendId).isBlocked(true).isDeleted(false).isApproved(true).build();

    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));

    // When

    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(FriendService.CANNOT_ADD_BLOCKED_USER);

    // Then

    then(friendshipRepository).shouldHaveNoInteractions();
  }

  @Test
  void itShouldThrowWhenPersonIsDeleted() {
    // Given
    long friendId = 5L;
    User friend = User.builder()
        .id(friendId).isBlocked(false).isDeleted(true).isApproved(true).build();

    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));

    // When

    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(FriendService.CANNOT_ADD_DELETED_USER);

    // Then

    then(friendshipRepository).shouldHaveNoInteractions();
  }

  @Test
  void itShouldThrowWhenPersonIsNotApproved() {
    // Given
    long friendId = 5L;
    User friend = User.builder()
        .id(friendId).isBlocked(false).isDeleted(false).isApproved(false).build();

    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));

    // When

    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(FriendService.CANNOT_ADD_NOT_APPROVED_USER);

    // Then

    then(friendshipRepository).shouldHaveNoInteractions();
  }

  @Test
  void itShouldThrowWhenPersonIsYourself() {
    // Given
    long friendId = 5L;
    User friend = User.builder()
        .id(friendId).isBlocked(false).isDeleted(false).isApproved(true).build();

    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(friend);

    // When

    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(FriendService.CANNOT_ADD_YOURSELF);

    // Then

    then(friendshipRepository).shouldHaveNoInteractions();
  }

  @Test
  void itShouldThrowWhenFirstIsPresentAndSecondIsEmpty() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();

    long friendId = 5L;
    User friend = User.builder()
        .id(friendId).isBlocked(false).isDeleted(false).isApproved(true).build();

    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    Friendship friendship = Friendship.builder()
        .id(17L)
        .status(FriendshipStatus.SUBSCRIBED)
        .srcPerson(user)
        .dstPerson(friend)
        .build();
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, friend)).willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(friend, user)).willReturn(Optional.empty());

    // When

    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(FriendService.NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 20));

    // Then
  }

  @Test
  void itShouldThrowWhenFirstIsEmptyAndSecondIsPresent() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();

    long friendId = 5L;
    User friend = User.builder()
        .id(friendId).isBlocked(false).isDeleted(false).isApproved(true).build();

    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    Friendship friendship = Friendship.builder()
        .id(17L)
        .status(FriendshipStatus.SUBSCRIBED)
        .srcPerson(user)
        .dstPerson(friend)
        .build();
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, friend)).willReturn(Optional.empty());
    given(friendshipRepository.findBySrcPersonAndDstPerson(friend, user)).willReturn(Optional.of(friendship));

    // When

    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(FriendService.NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 20));

    // Then
  }

  @Test
  void itShouldAddFriendSuccessfullyWhenBothOptionalsPresent() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();

    long friendId = 5L;
    User friend = User.builder()
        .id(friendId).isBlocked(false).isDeleted(false).isApproved(true).build();

    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    Friendship friendship = Friendship.builder()
        .id(17L)
        .status(FriendshipStatus.REQUEST)
        .srcPerson(user)
        .dstPerson(friend)
        .build();
    Friendship friendshipReverce = Friendship.builder()
        .id(18L)
        .status(FriendshipStatus.SUBSCRIBED)
        .srcPerson(friend)
        .dstPerson(user)
        .build();
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, friend))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(friend, user))
        .willReturn(Optional.of(friendshipReverce));

    // When

    underTest.addFriend(friendId);
  }
}
