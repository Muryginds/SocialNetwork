package ru.skillbox.zerone.backend.security.jwt;

import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.skillbox.zerone.backend.model.entity.Role;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public final class JwtUserFactory {

  public static JwtUser create(User user) {
    return new JwtUser(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        mapToGrantedAuthorities(user.getRoles()),
        user.getIsBlocked(),
        user.getStatus().equals(UserStatus.ACTIVE)
    );
  }

  private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles) {
    return userRoles.stream().map(role ->
        new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
  }
}