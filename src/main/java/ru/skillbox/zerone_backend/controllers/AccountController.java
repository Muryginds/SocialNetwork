package ru.skillbox.zerone_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zerone_backend.model.dto.requests.RegisterRequest;
import ru.skillbox.zerone_backend.model.dto.response.AccountResponse;
import ru.skillbox.zerone_backend.model.entities.User;
import ru.skillbox.zerone_backend.services.UserService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AccountResponse> register (@RequestBody RegisterRequest request) {
        var response = new AccountResponse();
        response.setData(Map.of("Message", "ok"));
        if (userService.findUserByEmail(request.getEmail()).isPresent()) {
            response.setError(String.format("User with email: %s already exists", request.getEmail()));
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok().body(response);
        }
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .regDate(LocalDateTime.now())
                .lastOnlineTime(LocalDateTime.now())
                .password(request.getPasswd1())
                .build();

        userService.save(user);

        return ResponseEntity.ok().body(response);
    }
}