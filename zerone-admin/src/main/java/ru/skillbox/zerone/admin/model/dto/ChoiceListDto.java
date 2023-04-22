package ru.skillbox.zerone.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceListDto {
  private Integer offset;
  private Integer limit;
  private Integer total;
  private List<ChoiceDto> choiceDtos;
}
