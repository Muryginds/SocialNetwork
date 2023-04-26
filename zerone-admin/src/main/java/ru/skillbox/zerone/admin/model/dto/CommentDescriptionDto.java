package ru.skillbox.zerone.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommentDescriptionDto {
  private String postAuthor;
  private String postTitle;
  private String commentAuthor;
  private String commentFragment;
  private String paramError;
}
