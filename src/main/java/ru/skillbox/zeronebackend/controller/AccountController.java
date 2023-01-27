package ru.skillbox.zeronebackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zeronebackend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zeronebackend.model.dto.response.ResponseDTO;
import ru.skillbox.zeronebackend.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<Object>> register (@RequestBody RegisterRequestDTO request) {
      return ResponseEntity.ok(userService.registerAccount(request));
    }
}