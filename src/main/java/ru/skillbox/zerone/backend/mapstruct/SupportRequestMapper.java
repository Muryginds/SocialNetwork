package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.zerone.backend.model.dto.request.SupportRequestDTO;
import ru.skillbox.zerone.backend.model.entity.SupportRequest;

@Mapper
public interface SupportRequestMapper {
  SupportRequestMapper INSTANCE = Mappers.getMapper(SupportRequestMapper.class);
  SupportRequest supportRequestDtoToSupportRequest(SupportRequestDTO requestDTO);
}
