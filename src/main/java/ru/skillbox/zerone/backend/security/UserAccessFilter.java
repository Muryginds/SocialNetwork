package ru.skillbox.zerone.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.io.IOException;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class UserAccessFilter extends GenericFilterBean {
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void doFilter(ServletRequest servletRequest,
                       ServletResponse servletResponse,
                       FilterChain filterChain) throws IOException, ServletException {
    String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);
    if (nonNull(token)) {
      var user = CurrentUserUtils.getCurrentUser();
      CurrentUserUtils.checkUserIsNotRestricted(user);
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
