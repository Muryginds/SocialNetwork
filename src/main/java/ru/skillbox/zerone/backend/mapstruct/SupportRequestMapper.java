package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import ru.skillbox.zerone.backend.model.dto.request.SupportRequestDTO;
import ru.skillbox.zerone.backend.model.entity.SupportRequest;

@Mapper
public interface SupportRequestMapper {

  SupportRequest supportRequestDtoToSupportRequest(SupportRequestDTO requestDTO);
}
