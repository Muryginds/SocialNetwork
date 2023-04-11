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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus.*;
import static ru.skillbox.zerone.backend.service.FriendService.*;

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
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
    utilsMockedStatic = Mockito.mockStatic(CurrentUserUtils.class);
    underTest = new FriendService(friendshipRepository, userRepository,
        userMapper, notificationService);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
    utilsMockedStatic.close();
  }

  @Test
  @SuppressWarnings("java:S5838")
  void testAddFriend_whenAllCorrect_thenCorrect() {
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
    var responseDTO = underTest.addFriend(friendId);

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
  void testAddFriend_whenPersonIsBlocked_thenException() {
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
  void testAddFriend_whenPersonIsDeleted_thenException() {
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
  void testAddFriend_whenPersonIsNotApproved_thenException() {
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
  void testAddFriend_whenPersonIsYourself_thenException() {
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
  void testAddFriend_whenFirstIsPresentAndSecondIsEmpty_thenException() {
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

    // When, Then
    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageStartingWith(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 15));
  }

  @Test
  void testAddFriend_whenFirstIsEmptyAndSecondIsPresent_thenException() {
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

    // When, Then
    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageStartingWith(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 15));
  }

  @Test
  void testAddFriend_whenBothOptionalsPresent_thenCorrect() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();

    long friendId = 5L;
    User friend = User.builder()
        .id(friendId).isBlocked(false).isDeleted(false).isApproved(true).build();

    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    Friendship friendship = Friendship.builder()
        .id(17L).status(REQUEST).srcPerson(user)
        .dstPerson(friend).build();
    Friendship friendshipReversed = Friendship.builder()
        .id(18L).status(SUBSCRIBED).srcPerson(friend)
        .dstPerson(user).build();
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, friend))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(friend, user))
        .willReturn(Optional.of(friendshipReversed));

    // When
    CommonResponseDTO<MessageResponseDTO> responseDTO = underTest.addFriend(friendId);

    // Then
    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");

    friendship.setStatus(DECLINED);
    friendshipReversed.setStatus(SUBSCRIBED);
    responseDTO = underTest.addFriend(friendId);
    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");

    friendship.setStatus(SUBSCRIBED);
    friendshipReversed.setStatus(DECLINED);
    responseDTO = underTest.addFriend(friendId);
    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");

    friendship.setStatus(DECLINED);
    friendshipReversed.setStatus(DECLINED);
    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageStartingWith(FriendService.WRONG_STATUS_COMBINATION.substring(0, 15));
  }

  @Test
  @SuppressWarnings("java:S5838")
  void testRemoveFriend_whenAllCorrect_thenCorrect() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long friendId = 5L;
    User friend = User.builder().id(friendId).build();
    Friendship friendship = Friendship.builder()
        .id(17L).status(FriendshipStatus.FRIEND)
        .srcPerson(user).dstPerson(friend)
        .build();
    Friendship friendshipReversed = Friendship.builder()
        .id(18L).status(FriendshipStatus.FRIEND)
        .dstPerson(friend).srcPerson(user)
        .build();
    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, friend))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(friend, user))
        .willReturn(Optional.of(friendshipReversed));

    // When
    var responseDTO = underTest.removeFriend(friendId);

    // Then
    ArgumentCaptor<List<Friendship>> friendshipArgumentCaptor = ArgumentCaptor.forClass(List.class);
    then(friendshipRepository).should().saveAll(friendshipArgumentCaptor.capture());
    List<Friendship> capture = friendshipArgumentCaptor.getValue();

    assertThat(capture.size()).isEqualTo(2);
    assertThat(capture.get(0).getStatus()).isEqualTo(FriendshipStatus.DECLINED);
    assertThat(capture.get(0).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(0).getDstPerson().getId()).isEqualTo(friendId);
    assertThat(capture.get(1).getStatus()).isEqualTo(FriendshipStatus.SUBSCRIBED);
    assertThat(capture.get(1).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(1).getDstPerson().getId()).isEqualTo(friendId);

    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");
  }

  @Test
  void testRemoveFriend_whenOneOptionalEmptyAndOneNotEmpty_thenException() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long friendId = 5L;
    User friend = User.builder().id(friendId).build();
    Friendship friendship = Friendship.builder()
        .id(17L).status(FriendshipStatus.FRIEND)
        .srcPerson(user).dstPerson(friend)
        .build();
    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, friend))
        .willReturn(Optional.empty());
    given(friendshipRepository.findBySrcPersonAndDstPerson(friend, user))
        .willReturn(Optional.of(friendship));

    // When, Then
    assertThatThrownBy(() -> underTest.removeFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageStartingWith(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 15));
  }

  @Test
  void testRemoveFriend_whenOneOptionalNotEmptyAndOneEmpty_thenException() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long friendId = 5L;
    User friend = User.builder().id(friendId).build();
    Friendship friendship = Friendship.builder()
        .id(17L).status(FriendshipStatus.FRIEND)
        .srcPerson(user).dstPerson(friend)
        .build();
    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, friend))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(friend, user))
        .willReturn(Optional.empty());

    // When, Then
    assertThatThrownBy(() -> underTest.removeFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageStartingWith(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 15));
  }

  @Test
  void testRemoveFriend_whenBothOptionalEmpty_thenException() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long friendId = 5L;
    User friend = User.builder().id(friendId).build();
    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    // When, Then
    assertThatThrownBy(() -> underTest.removeFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(FriendService.USERS_ARE_NOT_FRIENDS);
  }

  @Test
  void testRemoveFriend_whenBothAreNotFriends_thenException() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long friendId = 5L;
    User friend = User.builder().id(friendId).build();
    Friendship friendship = Friendship.builder()
        .id(17L).status(FriendshipStatus.DECLINED)
        .srcPerson(user).dstPerson(friend)
        .build();
    Friendship friendshipReversed = Friendship.builder()
        .id(18L).status(FriendshipStatus.DECLINED)
        .dstPerson(friend).srcPerson(user)
        .build();
    given(userRepository.findById(friendId)).willReturn(Optional.of(friend));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, friend))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(friend, user))
        .willReturn(Optional.of(friendshipReversed));

    // When, Then
    assertThatThrownBy(() -> underTest.removeFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(FriendService.USERS_ARE_NOT_FRIENDS);
  }

  @Test
  void testRemoveFriend_CheckNotAllowedStatusCombinations() throws NoSuchMethodException {
    // Given
    Method method = FriendService.class.getDeclaredMethod(
        "checkNotAllowedStatusCombinationsAndThrowExceptionIfMatched",
        FriendshipStatus.class, FriendshipStatus.class);
    method.setAccessible(true);

    // When, Then
    assertThatThrownBy(() -> method.invoke(underTest, DEADLOCK, DEADLOCK))
        .hasCauseInstanceOf(FriendshipException.class);
    assertThatThrownBy(() -> method.invoke(underTest, BLOCKED, WASBLOCKEDBY))
        .hasCauseInstanceOf(FriendshipException.class);
    assertThatThrownBy(() -> method.invoke(underTest, WASBLOCKEDBY, BLOCKED))
        .hasCauseInstanceOf(FriendshipException.class);
    assertThatThrownBy(() -> method.invoke(underTest, FRIEND, FRIEND))
        .hasCauseInstanceOf(FriendshipException.class);
    assertThatThrownBy(() -> method.invoke(underTest, SUBSCRIBED, REQUEST))
        .hasCauseInstanceOf(FriendshipException.class);
  }

  @Test
  @SuppressWarnings("java:S5838")
  void testBlockFriend_whenAllCorrect_thenCorrect() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long targetId = 5L;
    User target = User.builder().id(targetId).build();
    Friendship friendship = Friendship.builder().id(17L)
        .status(FRIEND).srcPerson(user)
        .dstPerson(target).build();
    Friendship friendshipReversed = Friendship.builder().id(18L)
        .status(FRIEND).srcPerson(target)
        .dstPerson(user).build();

    given(userRepository.findById(targetId)).willReturn(Optional.of(target));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.of(friendshipReversed));

    // When
    var responseDTO = underTest.blockUser(targetId);

    // Then
    ArgumentCaptor<List<Friendship>> friendshipArgumentCaptor = ArgumentCaptor.forClass(List.class);
    then(friendshipRepository).should().saveAll(friendshipArgumentCaptor.capture());
    List<Friendship> capture = friendshipArgumentCaptor.getValue();

    assertThat(capture.size()).isEqualTo(2);
    assertThat(capture.get(0).getStatus()).isEqualTo(BLOCKED);
    assertThat(capture.get(0).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(0).getDstPerson().getId()).isEqualTo(targetId);
    assertThat(capture.get(1).getStatus()).isEqualTo(WASBLOCKEDBY);
    assertThat(capture.get(1).getSrcPerson().getId()).isEqualTo(targetId);
    assertThat(capture.get(1).getDstPerson().getId()).isEqualTo(userId);

    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");

  }

  @Test
  void testBlockFriend_whenUserEqualsTarget_thenException() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    // When
    assertThatThrownBy(() -> underTest.blockUser(userId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(FriendService.CANNOT_ADD_YOURSELF);

    // Then
    then(friendshipRepository).shouldHaveNoInteractions();
  }

  @Test
  void testBlockFriend_whenOneOptionalEmptyAndOneNotEmpty_thenException() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long targetId = 5L;
    User target = User.builder().id(targetId).build();
    Friendship friendship = Friendship.builder().id(17L)
        .status(FRIEND).srcPerson(user)
        .dstPerson(target).build();
    Friendship friendshipReversed = Friendship.builder().id(18L)
        .status(FRIEND).srcPerson(target)
        .dstPerson(user).build();

    given(userRepository.findById(targetId)).willReturn(Optional.of(target));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.empty());

    // When, Then
    assertThatThrownBy(() -> underTest.blockUser(targetId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 15));

    // Given
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.empty());
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.of(friendshipReversed));

    // When, Then
    assertThatThrownBy(() -> underTest.blockUser(targetId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 15));
  }

  @Test
  void testBlockFriend_whenBothOptionalEmpty_thenCorrect() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long targetId = 5L;
    User target = User.builder().id(targetId).build();

    given(userRepository.findById(targetId)).willReturn(Optional.of(target));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    // When
    var responseDTO = underTest.blockUser(targetId);

    // Then
    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");
  }

  @Test
  void testBlockFriend_whenTargetIsAlreadyBlocked_thenException() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long targetId = 5L;
    User target = User.builder().id(targetId).build();
    Friendship friendship = Friendship.builder().id(17L)
        .status(DEADLOCK).srcPerson(user)
        .dstPerson(target).build();
    Friendship friendshipReversed = Friendship.builder().id(18L)
        .status(DEADLOCK).srcPerson(target)
        .dstPerson(user).build();

    given(userRepository.findById(targetId)).willReturn(Optional.of(target));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.of(friendshipReversed));

    // When, Then
    assertThatThrownBy(() -> underTest.blockUser(targetId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(YOU_HAVE_ALREADY_BLOCKED_HEEM);

    // Given
    friendship.setStatus(BLOCKED);
    friendshipReversed.setStatus(WASBLOCKEDBY);

    // When, Then
    assertThatThrownBy(() -> underTest.blockUser(targetId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(YOU_HAVE_ALREADY_BLOCKED_HEEM);
  }

  @Test
  void testBlockFriend_whenOneWasBlockedByAndAnotherBlocked_thenCorrect() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long targetId = 5L;
    User target = User.builder().id(targetId).build();
    Friendship friendship = Friendship.builder().id(17L)
        .status(WASBLOCKEDBY).srcPerson(user)
        .dstPerson(target).build();
    Friendship friendshipReversed = Friendship.builder().id(18L)
        .status(BLOCKED).srcPerson(target)
        .dstPerson(user).build();

    given(userRepository.findById(targetId)).willReturn(Optional.of(target));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.of(friendshipReversed));

    // When
    var responseDTO = underTest.blockUser(targetId);

    // Then
    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");
  }

  @Test
  @SuppressWarnings("java:S5838")
  void testUnblockUser_whenBothOptionalPresent_thenCorrect() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();

    long targetId = 5L;
    User target = User.builder().id(targetId).build();

    given(userRepository.findById(targetId)).willReturn(Optional.of(target));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    var friendship = Friendship.builder()
        .srcPerson(user).dstPerson(target)
        .status(BLOCKED).build();
    var friendshipReversed = Friendship.builder()
        .srcPerson(target).dstPerson(user)
        .status(WASBLOCKEDBY).build();
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.of(friendshipReversed));

    // When
    var responseDTO = underTest.unblockUser(targetId);

    // Then
    ArgumentCaptor<List<Friendship>> friendshipArgumentCaptor = ArgumentCaptor.forClass(List.class);
    then(friendshipRepository).should().deleteAll(friendshipArgumentCaptor.capture());
    List<Friendship> capture = friendshipArgumentCaptor.getValue();

    assertThat(capture.size()).isEqualTo(2);
    assertThat(capture.get(0).getStatus()).isEqualTo(BLOCKED);
    assertThat(capture.get(0).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(0).getDstPerson().getId()).isEqualTo(targetId);
    assertThat(capture.get(1).getStatus()).isEqualTo(WASBLOCKEDBY);
    assertThat(capture.get(1).getSrcPerson().getId()).isEqualTo(targetId);
    assertThat(capture.get(1).getDstPerson().getId()).isEqualTo(userId);

    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");
  }

  @Test
  void testUnblockUser_whenOneOptionalEmptyAndOneNotEmpty_thenException() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long targetId = 5L;
    User target = User.builder().id(targetId).build();

    given(userRepository.findById(targetId)).willReturn(Optional.of(target));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    var friendship = Friendship.builder()
        .srcPerson(user).dstPerson(target)
        .status(FRIEND).build();
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.empty());

    // When, Then
    assertThatThrownBy(() -> underTest.unblockUser(targetId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 15));

    // Given
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.empty());
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.of(friendship));

    // When, Then
    assertThatThrownBy(() -> underTest.unblockUser(targetId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(NO_PAIR_FOUND_FOR_RECORD_WITH_ID_PATTERN.substring(0, 15));
  }

  @Test
  void testUnblockUser_whenBothOptionalAreDEADLOCK_thenCorrect() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();

    long targetId = 5L;
    User target = User.builder().id(targetId).build();

    given(userRepository.findById(targetId)).willReturn(Optional.of(target));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    var friendship = Friendship.builder()
        .srcPerson(user).dstPerson(target)
        .status(DEADLOCK).build();
    var friendshipReversed = Friendship.builder()
        .srcPerson(target).dstPerson(user)
        .status(DEADLOCK).build();
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.of(friendshipReversed));

    // When
    var responseDTO = underTest.unblockUser(targetId);

    // Then
    assertThat(responseDTO.getData().getMessage()).isEqualTo("OK");
  }

  @Test
  void testUnblockUser_whenTargetNotBlocked_thenException() {
    // Given
    long userId = 1L;
    User user = User.builder().id(userId).build();
    long targetId = 5L;
    User target = User.builder().id(targetId).build();

    given(userRepository.findById(targetId)).willReturn(Optional.of(target));
    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    var friendship = Friendship.builder().srcPerson(user).dstPerson(target)
        .status(FRIEND).build();
    var friendshipReversed = Friendship.builder().srcPerson(target).dstPerson(user)
        .status(FRIEND).build();
    given(friendshipRepository.findBySrcPersonAndDstPerson(user, target))
        .willReturn(Optional.of(friendship));
    given(friendshipRepository.findBySrcPersonAndDstPerson(target, user))
        .willReturn(Optional.of(friendshipReversed));

    // When, Then
    assertThatThrownBy(() -> underTest.unblockUser(targetId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(USER_NOT_BLOCKED);
  }
}
