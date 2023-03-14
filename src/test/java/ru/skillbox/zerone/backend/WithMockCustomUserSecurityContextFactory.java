package ru.skillbox.zerone.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.service.RoleService;

import java.util.List;

@Component
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

  @Autowired
  private RoleService roleService;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    var user = User.builder()
        .email(customUser.email())
        .firstName(customUser.firstName())
        .lastName(customUser.lastName())
        .password(passwordEncoder.encode(customUser.email()))
        .isApproved(true)
        .confirmationCode("test")
        .roles(List.of(roleService.getBasicUserRole()))
        .build();

    Authentication auth =
        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    context.setAuthentication(auth);

    return context;
  }
}
