package ru.skillbox.zerone_backend.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicResponse {
    private String error;
    private LocalDateTime timestamp;
}