package ru.skillbox.zerone.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.BlacklistException;
import ru.skillbox.zerone.backend.model.entity.BlacklistToken;
import ru.skillbox.zerone.backend.repository.BlacklistRepository;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class BlacklistService {

  @Value("${blacklist.deletionInterval}")
  private int interval;

  private static AtomicLong count = new AtomicLong();

  private final BlacklistRepository blacklistRepository;

  @Value("${jwt.token.secret}")
  private String secret;
  @PostConstruct
  protected void init() {
    secret = Base64.getEncoder().encodeToString(secret.getBytes());
  }

  @Transactional
  public void processLogout(String token) throws JsonProcessingException {
    Date expiration = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
        .getBody().getExpiration();
    var blacklistToken = BlacklistToken.builder()
        .token(token)
        .expired(expiration)
        .build();
    blacklistRepository.save(blacklistToken);
    Long newCount = count.incrementAndGet();
    if  (newCount % interval == 0) {
      blacklistRepository.deleteByExpiredLessThan(new Date());
    }
  }

  public void validateToken(String token) {
    var blacklistToken = blacklistRepository.findByToken(token).orElse(null);
    if (blacklistToken != null) {
      throw new BlacklistException(token);
    }
  }
}
