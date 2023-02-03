package ru.skillbox.zerone.backend.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@RequiredArgsConstructor
public class JwtUser implements UserDetails {

  private final Long id;
  private final String password;
  private final String email;
  private final boolean enabled;

  private final Collection<? extends GrantedAuthority> authorities;

  public JwtUser(
      Long id,
      String email,
      String password, Collection<? extends GrantedAuthority> authorities,
      boolean enabled
  ) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.authorities = authorities;
    this.enabled = enabled;
  }


  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }


  @JsonIgnore
  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

}
