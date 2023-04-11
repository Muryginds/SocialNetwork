package ru.skillbox.zerone.backend.service;

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
  private static final AtomicLong COUNT = new AtomicLong();
  private final BlacklistRepository blacklistRepository;
  @Value("${blacklist.deletionInterval}")
  private int interval;
  @Value("${jwt.token.secret}")
  private String secret;

  @PostConstruct
  protected void init() {
    secret = Base64.getEncoder().encodeToString(secret.getBytes());
  }

  @Transactional
  @SuppressWarnings("java:S1874")
  public void processLogout(String token) {
    var expiration = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
        .getBody().getExpiration();
    var blacklistToken = BlacklistToken.builder()
        .token(token)
        .expired(expiration)
        .build();
    if (blacklistRepository.existsByToken(token)) {
      return;
    }
    blacklistRepository.save(blacklistToken);
    long newCount = COUNT.incrementAndGet();
    if  (newCount % interval == 0) {
      blacklistRepository.deleteByExpiredLessThan(new Date());
    }
  }

  public void validateToken(String token) {
    if (blacklistRepository.existsByToken(token)) {
      throw new BlacklistException("Token is in Blacklist");
    }
  }
}
