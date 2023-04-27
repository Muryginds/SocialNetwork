package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import ru.skillbox.zerone.backend.model.dto.request.MessageRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageDataDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.DialogRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
class DialogServiceTest extends AbstractIntegrationTest {

  @Autowired
  private DialogRepository dialogRepository;
  @Autowired
  private DialogService dialogService;

  User currentTestUser;
  private final MockedStatic<CurrentUserUtils> utilsMockedStatic = Mockito.mockStatic(CurrentUserUtils.class);
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    currentTestUser = new User().setId(1L);
    utilsMockedStatic.when(() -> CurrentUserUtils.getCurrentUser()).thenReturn(currentTestUser);
  }

  @AfterEach
  void tearDown() {
    utilsMockedStatic.close();
  }

  @Test
  @Sql(scripts = "classpath:mock-dialog-insert.sql")
  void testDialogService_when_ReadAllMessage_thenReturnCorrectDto() {

    int MaxId = jdbcTemplate.queryForObject("SELECT coalesce (max(d.id),0) as ma_id FROM Dialog d", Integer.class);
    var result = dialogService.getMessages(MaxId, 0, 10);

    List<MessageDataDTO> messageDataDTOList = new ArrayList<>();
    var MessageDate = LocalDateTime.of(2023, 1, 27, 17, 58,18,480000000);
    MessageDataDTO messageDataDTO1 = new MessageDataDTO("текст1SENT", false, "SENT", 1,1, MessageDate, 102);
    MessageDataDTO messageDataDTO2 = new MessageDataDTO("текст2READ", false, "READ", 1,2, MessageDate, 102);
    messageDataDTOList.add(messageDataDTO1);
    messageDataDTOList.add(messageDataDTO2);

    assertNotNull(result);
    assertEquals(2, result.getTotal());
    assertEquals(2, result.getData().size());
    assertEquals(messageDataDTOList.get(0).getMessageText(), getResultByIndex(result,0).getMessageText());
    assertEquals(messageDataDTOList.get(1).getMessageText(), getResultByIndex(result,1).getMessageText());
    assertEquals("READ", getResultByIndex(result,0).getReadStatus());
    assertEquals("READ", getResultByIndex(result,1).getReadStatus());
    assertEquals(messageDataDTOList.get(0).isSendByMe(), getResultByIndex(result,0).isSendByMe());
    assertEquals(messageDataDTOList.get(1).isSendByMe(), getResultByIndex(result,1).isSendByMe());
    assertEquals(messageDataDTOList.get(0).getTime(), getResultByIndex(result,0).getTime());
  }

  private MessageDataDTO getResultByIndex(CommonListResponseDTO<MessageDataDTO> result, Integer ind) {
    return result.getData().get(ind);
  }


  @Test
  @Sql(scripts = "classpath:mock-dialog-insert.sql")
  void testPostMessage() {
    MessageRequestDTO messageRequestDTO = new MessageRequestDTO();
    messageRequestDTO.setMessageText("test message");

    CommonResponseDTO<MessageDataDTO> response = dialogService.postMessages(dialogRepository.findMaxId(), messageRequestDTO);

    assertEquals("test message", response.getData().getMessageText());
    assertEquals("SENT", response.getData().getReadStatus());
    assertNotNull(dialogRepository.findById(response.getData().getDialogId()));
  }




}
