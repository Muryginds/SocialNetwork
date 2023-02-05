package ru.skillbox.zerone.backend.security.jwt;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.entity.Role;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  @Value("${jwt.token.secret}")
  private String secret;
  @Value("${jwt.token.expired}")
  private long validityInMilliseconds;
  private final UserDetailsService userDetailsService;

  @PostConstruct
  protected void init() {
    secret = Base64.getEncoder().encodeToString(secret.getBytes());
  }

  public String createToken(String email, List<Role> roles) {

    Claims claims = Jwts.claims().setSubject(email);
    claims.put("roles", getRoleNames(roles));

    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    var userDetails = userDetailsService.loadUserByUsername(getUsername(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getUsername(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
  }

  public String resolveToken(HttpServletRequest req) {
    return req.getHeader("Authorization");
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

      return claims.getBody().getExpiration().after(new Date());

    } catch (JwtException | IllegalArgumentException e) {
      throw new JwtException("JWT token is expired or invalid");
    }
  }

  private List<String> getRoleNames(List<Role> userRoles) {
    return userRoles.stream()
        .map(Role::getName).collect(Collectors.toList());
  }
}