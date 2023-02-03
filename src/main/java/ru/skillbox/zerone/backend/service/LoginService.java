package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.UserDto;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDto;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;


    public CommonResponseDTO<UserDto> login(AuthRequestDto requestDto) {

      try {
        String email = requestDto.getEmail();
        User user = userService.findByEmail(email);

        if (user == null) {
          throw new UsernameNotFoundException("User with email: " + email + " not found");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, requestDto.getPassword()));

        String token = jwtTokenProvider.createToken(email, user.getRoles());
        System.out.println(token);

        UserDto userDto = userService.fromUser(user);
        userDto.setToken(token);
        CommonResponseDTO<UserDto> responseDto = new CommonResponseDTO<>();
        responseDto.setData(userDto);

        return responseDto;

      } catch (AuthenticationException e) {
        throw new BadCredentialsException("Invalid username or password");
      }
    }
}