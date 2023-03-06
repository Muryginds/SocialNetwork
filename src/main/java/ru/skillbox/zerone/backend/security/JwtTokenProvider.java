package ru.skillbox.zerone.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.entity.Role;
import ru.skillbox.zerone.backend.service.BlacklistService;
import ru.skillbox.zerone.backend.service.JpaUserDetails;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
  @Value("${jwt.token.secret}")
  private String secret;

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(this.secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Value("${jwt.token.expired}")
  private long validityInMilliseconds;
  private final JpaUserDetails jpaUserDetails;
  private final BlacklistService blacklistService;

  @PostConstruct
  protected void init() {
    secret = Base64.getEncoder().encodeToString(secret.getBytes());
  }


  public String createToken(String email, List<Role> roles) {

    Claims claims = Jwts.claims().setSubject(email);
    claims.put("roles", roles);

    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(getSigningKey()) //SignatureAlgorithm.HS256, secret
        .compact();
  }

  public Authentication getAuthentication(String token) {
    var user = jpaUserDetails.loadUserByUsername(getUsername(token));
    return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
  }

  public String getUsername(String token) {
    return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody().getSubject();
  }

  public String resolveToken(HttpServletRequest req) {
    return req.getHeader("Authorization");
  }

  public boolean validateToken(String token) {
    blacklistService.validateToken(token);
    Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
    return claims.getBody().getExpiration().after(new Date());
  }
}