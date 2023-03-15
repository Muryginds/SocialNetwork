package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;

@Data
public class NotificationSettingListDTO {
  private Boolean postEnabled;
  private Boolean postCommentEnabled;
  private Boolean commentCommentEnabled;
  private Boolean friendRequestEnabled;
  private Boolean messagesEnabled;
  private Boolean friendBirthdayEnabled;
}
