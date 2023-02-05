package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class LoginService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  public CommonResponseDTO<UserDTO> login(AuthRequestDTO requestDto) {

    try {
      String email = requestDto.getEmail();
      var userOptional = userRepository.findUserByEmail(email);

      if (userOptional.isEmpty()) {
        throw new UsernameNotFoundException(String.format("User with email: %s not found", email));
      }

      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, requestDto.getPassword()));

      User user = userOptional.get();
      String token = jwtTokenProvider.createToken(email, user.getRoles());

      UserDTO userDTO = UserDTO.fromUser(user);
      userDTO.setToken(token);

      CommonResponseDTO<UserDTO> responseDTO = new CommonResponseDTO<>();
      responseDTO.setData(userDTO);

      return responseDTO;

    } catch (AuthenticationException e) {
      throw new BadCredentialsException("Invalid username or password");
    }
  }
}