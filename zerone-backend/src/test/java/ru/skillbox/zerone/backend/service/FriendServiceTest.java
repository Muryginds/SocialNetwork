package ru.skillbox.zerone.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.skillbox.zerone.backend.exception.FriendshipException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.IsFriendsDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.model.entity.Friendship;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus;
import ru.skillbox.zerone.backend.repository.FriendshipRepository;
import ru.skillbox.zerone.backend.repository.RecommendationRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus.*;
import static ru.skillbox.zerone.backend.service.FriendService.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {
  private static final String OK = "OK";

  private AutoCloseable utilsMockedStatic;
  @Mock
  private FriendshipRepository friendshipRepository;
  @Mock
  RecommendationRepository recommendationRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private FriendService underTest;
  @Captor
  ArgumentCaptor<List<Friendship>> friendshipListCaptor;

  @BeforeEach
  void setUp() {
    utilsMockedStatic = Mockito.mockStatic(CurrentUserUtils.class);
  }

  @AfterEach
  void tearDown() throws Exception {
    utilsMockedStatic.close();
  }

  @Test
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
    then(friendshipRepository).should().saveAll(friendshipListCaptor.capture());
    List<Friendship> capture = friendshipListCaptor.getValue();

    assertThat(capture).hasSize(2);
    assertThat(capture.get(0).getStatus()).isEqualTo(FriendshipStatus.SUBSCRIBED);
    assertThat(capture.get(0).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(0).getDstPerson().getId()).isEqualTo(friendId);
    assertThat(capture.get(1).getStatus()).isEqualTo(FriendshipStatus.REQUEST);
    assertThat(capture.get(1).getSrcPerson().getId()).isEqualTo(friendId);
    assertThat(capture.get(1).getDstPerson().getId()).isEqualTo(userId);

    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);
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
    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);

    friendship.setStatus(DECLINED);
    friendshipReversed.setStatus(SUBSCRIBED);
    responseDTO = underTest.addFriend(friendId);
    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);

    friendship.setStatus(SUBSCRIBED);
    friendshipReversed.setStatus(DECLINED);
    responseDTO = underTest.addFriend(friendId);
    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);

    friendship.setStatus(DECLINED);
    friendshipReversed.setStatus(DECLINED);
    assertThatThrownBy(() -> underTest.addFriend(friendId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageStartingWith(FriendService.WRONG_STATUS_COMBINATION.substring(0, 15));
  }

  @Test
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
    then(friendshipRepository).should().saveAll(friendshipListCaptor.capture());
    List<Friendship> capture = friendshipListCaptor.getValue();

    assertThat(capture).hasSize(2);
    assertThat(capture.get(0).getStatus()).isEqualTo(FriendshipStatus.DECLINED);
    assertThat(capture.get(0).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(0).getDstPerson().getId()).isEqualTo(friendId);
    assertThat(capture.get(1).getStatus()).isEqualTo(FriendshipStatus.SUBSCRIBED);
    assertThat(capture.get(1).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(1).getDstPerson().getId()).isEqualTo(friendId);

    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);
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
    then(friendshipRepository).should().saveAll(friendshipListCaptor.capture());
    List<Friendship> capture = friendshipListCaptor.getValue();

    assertThat(capture).hasSize(2);
    assertThat(capture.get(0).getStatus()).isEqualTo(BLOCKED);
    assertThat(capture.get(0).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(0).getDstPerson().getId()).isEqualTo(targetId);
    assertThat(capture.get(1).getStatus()).isEqualTo(WASBLOCKEDBY);
    assertThat(capture.get(1).getSrcPerson().getId()).isEqualTo(targetId);
    assertThat(capture.get(1).getDstPerson().getId()).isEqualTo(userId);

    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);

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
    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);
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
        .hasMessageContaining(YOU_HAVE_ALREADY_BLOCKED_HIM);

    // Given
    friendship.setStatus(BLOCKED);
    friendshipReversed.setStatus(WASBLOCKEDBY);

    // When, Then
    assertThatThrownBy(() -> underTest.blockUser(targetId))
        .isInstanceOf(FriendshipException.class)
        .hasMessageContaining(YOU_HAVE_ALREADY_BLOCKED_HIM);
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
    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);
  }

  @Test
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
    then(friendshipRepository).should().deleteAll(friendshipListCaptor.capture());
    List<Friendship> capture = friendshipListCaptor.getValue();

    assertThat(capture).hasSize(2);
    assertThat(capture.get(0).getStatus()).isEqualTo(BLOCKED);
    assertThat(capture.get(0).getSrcPerson().getId()).isEqualTo(userId);
    assertThat(capture.get(0).getDstPerson().getId()).isEqualTo(targetId);
    assertThat(capture.get(1).getStatus()).isEqualTo(WASBLOCKEDBY);
    assertThat(capture.get(1).getSrcPerson().getId()).isEqualTo(targetId);
    assertThat(capture.get(1).getDstPerson().getId()).isEqualTo(userId);

    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);
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
        .id(1_000L)
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
    assertThat(responseDTO.getData().getMessage()).isEqualTo(OK);
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


  @Test
  void testCheckIsFriends_whenAllCorrect_thenCorrect() {
    // Given
    IsFriendsDTO isFriendsDTO = new IsFriendsDTO();
    isFriendsDTO.setUserIds(List.of(5L, 7L, 9L, 11L));

    User user = User.builder().id(1L).build();
    User user2 = User.builder().id(2L).build();

    User dest1 = User.builder().id(5L).build();
    User dest2 = User.builder().id(7L).build();
    User dest3 = User.builder().id(9L).build();
    User dest4 = User.builder().id(11L).build();
    User dest5 = User.builder().id(13L).build();

    List<Friendship> friends = List.of(
        Friendship.builder().id(1L).srcPerson(user).dstPerson(dest1).status(FRIEND).build(),
        Friendship.builder().id(2L).srcPerson(user).dstPerson(dest2).status(FRIEND).build(),
        Friendship.builder().id(3L).srcPerson(user).dstPerson(dest3).status(BLOCKED).build(),
        Friendship.builder().id(4L).srcPerson(user2).dstPerson(dest4).status(FRIEND).build(),
        Friendship.builder().id(5L).srcPerson(user).dstPerson(dest5).status(FRIEND).build()
    );

    given(CurrentUserUtils.getCurrentUser()).willReturn(user);
    given(friendshipRepository.findAllBySrcPersonAndDstPersonIdIn(user, isFriendsDTO.getUserIds()))
        .willReturn(friends.stream()
            .filter(fr -> fr.getSrcPerson().equals(user))
            .filter(fr -> isFriendsDTO.getUserIds().contains(fr.getDstPerson().getId()))
            .toList());

    // When
    var statusFriendDTO = underTest.checkIsFriends(isFriendsDTO);

    // Then
    List<StatusFriendDTO> data = statusFriendDTO.getData();
    List<Long> ids = data.stream().map(StatusFriendDTO::getUserId).toList();

    assertThat(data).hasSize(3);
    assertThat(ids).isEqualTo(List.of(5L, 7L, 9L));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetPageOfFriendsByFriendStatus() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Given
    String keyName = "Andrew";
    User user = User.builder().id(1L).build();
    User user2 = User.builder().id(2L).build();
    User person1 = User.builder().id(11L).firstName(keyName).build();
    User person2 = User.builder().id(11L).firstName("Paul").build();
    User person3 = User.builder().id(11L).firstName("Ivan").build();
    User person4 = User.builder().id(11L).firstName(keyName).build();
    User person5 = User.builder().id(11L).firstName("Stephen").build();


    List<Friendship> friendships = List.of(
        Friendship.builder().id(1L).status(FRIEND).srcPerson(user).dstPerson(person1).build(),
        Friendship.builder().id(2L).status(FRIEND).srcPerson(user).dstPerson(person2).build(),
        Friendship.builder().id(3L).status(FRIEND).srcPerson(user).dstPerson(person3).build(),
        Friendship.builder().id(4L).status(FRIEND).srcPerson(user).dstPerson(person4).build(),
        Friendship.builder().id(5L).status(FRIEND).srcPerson(user2).dstPerson(person3).build(),
        Friendship.builder().id(6L).status(BLOCKED).srcPerson(user).dstPerson(person5).build()
    );

    PageRequest pageRequest = PageRequest.of(0, 5);

    Method method = FriendService.class.getDeclaredMethod(
        "getPageOfFriendsByFriendStatus",
        FriendshipStatus.class, String.class, int.class, int.class);
    method.setAccessible(true);

    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    Page<Friendship> pageToReturn = new PageImpl<>(
        friendships.stream()
            .filter(fr -> fr.getSrcPerson().equals(user))
            .filter(fr ->  fr.getStatus().equals(FRIEND))
            .toList()
    );
    given(friendshipRepository.findAllBySrcPersonAndStatus(user, FRIEND, pageRequest))
        .willReturn(pageToReturn);

    // When
    var page = (Page<Friendship>) method.invoke(
        underTest, FRIEND, "",
        (int) pageRequest.getOffset(), pageRequest.getPageSize());

    // Then
    assertThat(page.getTotalElements()).isEqualTo(4);

    // Giver
    pageToReturn = new PageImpl<>(
        friendships.stream()
            .filter(fr -> fr.getSrcPerson().equals(user))
            .filter(fr ->  fr.getStatus().equals(FRIEND))
            .filter(fr -> fr.getDstPerson().getFirstName().equals(keyName))
            .toList()
    );
    given(friendshipRepository.findAllBySrcPersonAndStatusAndDstPersonNameLike(user, FRIEND, keyName, pageRequest))
        .willReturn(pageToReturn);

    // When
    page = (Page<Friendship>) method.invoke(
        underTest, FRIEND, keyName,
        (int) pageRequest.getOffset(), pageRequest.getPageSize());

    // Then
    assertThat(page.getTotalElements()).isEqualTo(2);
  }

  @Test
  void testGetFriendList_whenAllCorrect_thenCorrect() {
    CommonListResponseDTO<UserDTO> friendList = getFriendListAndGetFriendRequestList(FRIEND);
    assertThat(friendList.getTotal()).isEqualTo(3);
  }

  @Test
  void testGetFriendRequestList_whenAllCorrect_thenCorrect() {
    CommonListResponseDTO<UserDTO> friendList = getFriendListAndGetFriendRequestList(REQUEST);
    assertThat(friendList.getTotal()).isEqualTo(1);
  }

  private CommonListResponseDTO<UserDTO> getFriendListAndGetFriendRequestList(FriendshipStatus status) {
    // Given
    User user = User.builder().id(1L).build();
    User user2 = User.builder().id(2L).build();
    User person1 = User.builder().id(11L).firstName("Andrew").build();
    User person2 = User.builder().id(12L).firstName("Paul").build();
    User person3 = User.builder().id(13L).firstName("Ivan").build();
    User person4 = User.builder().id(14L).firstName("Andrew").build();
    User person5 = User.builder().id(15L).firstName("Stephen").build();


    List<Friendship> friendships = List.of(
        Friendship.builder().id(1L).status(FRIEND).srcPerson(user).dstPerson(person1).build(),
        Friendship.builder().id(2L).status(REQUEST).srcPerson(user).dstPerson(person2).build(),
        Friendship.builder().id(3L).status(FRIEND).srcPerson(user).dstPerson(person3).build(),
        Friendship.builder().id(4L).status(FRIEND).srcPerson(user).dstPerson(person4).build(),
        Friendship.builder().id(5L).status(FRIEND).srcPerson(user2).dstPerson(person3).build(),
        Friendship.builder().id(6L).status(BLOCKED).srcPerson(user).dstPerson(person5).build()
    );

    PageRequest pageRequest = PageRequest.of(0, 5);

    given(CurrentUserUtils.getCurrentUser()).willReturn(user);

    Page<Friendship> pageToReturn = new PageImpl<>(
        friendships.stream()
            .filter(fr -> fr.getSrcPerson().equals(user))
            .filter(fr ->  fr.getStatus().equals(status))
            .toList()
    );
    List<User> friends = pageToReturn.map(Friendship::getDstPerson).toList();
    List<UserDTO> dtos = new ArrayList<>();
    friends.forEach(person -> {
      UserDTO dto = UserDTO.builder()
          .id(person.getId())
          .firstName(person.getFirstName()).build();
      dtos.add(dto);
    });

    given(friendshipRepository.findAllBySrcPersonAndStatus(user, status, pageRequest))
        .willReturn(pageToReturn);
    given(userMapper.usersToUserDTO(friends))
        .willReturn(dtos);


    // When
    CommonListResponseDTO<UserDTO> friendList;
    if (status == FRIEND) {
      friendList = underTest.getFriendList("", (int) pageRequest.getOffset(), pageRequest.getPageSize());
    } else {
      friendList = underTest.getFriendRequestList("", (int) pageRequest.getOffset(), pageRequest.getPageSize());
    }

    // Then
    return friendList;
  }

}
