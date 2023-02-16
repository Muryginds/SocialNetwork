package ru.skillbox.zerone.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.BlacklistException;
import ru.skillbox.zerone.backend.model.entity.BlacklistToken;
import ru.skillbox.zerone.backend.repository.BlacklistRepository;
import ru.skillbox.zerone.backend.util.TokenDecoder;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BlacklistService {

  private final BlacklistRepository blacklistRepository;

  public void processLogout(String token) throws JsonProcessingException {
    var tokenUtils = new TokenDecoder(token);
    Date expired = tokenUtils.getExpired();
    var blacklistToken = BlacklistToken.builder()
        .token(token)
        .expired(expired)
        .build();
    blacklistRepository.save(blacklistToken);
  }

  public void validateToken(String token) {
    var blacklistToken = blacklistRepository.findByToken(token).orElse(null);
    if (blacklistToken != null) {
      throw new BlacklistException(token);
    }
  }
}
