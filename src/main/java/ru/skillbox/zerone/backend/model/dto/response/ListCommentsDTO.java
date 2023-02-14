package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ListCommentsDTO<T> {
private long total;
private long perPage;
private long offset;
private String error;
private LocalDateTime timestamp = LocalDateTime.now();
}
