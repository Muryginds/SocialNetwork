package ru.skillbox.zeronebackend.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class AccountResponse extends BasicResponse {
    private Map<String, String> data;

    public AccountResponse(String error, LocalDateTime timestamp, Map<String, String> data) {
        super(error, timestamp);
        this.data = data;
    }
}