package ru.skillbox.zerone.admin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private static final String LOGIN_ADDRESS = "/api/v1/admin/login";
  private final PasswordEncoder passwordEncoder;
  private final UserDetailsService userDetailsService;
  private final LoadBalancerClient loadBalancerClient;

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      DaoAuthenticationProvider daoAuthenticationProvider,
      String apiGatewayHost,
      SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler,
      AuthenticationEntryPoint authenticationEntryPoint) throws Exception {

    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authz -> authz
                .anyRequest().hasRole("ADMIN")
        )
        .authenticationProvider(daoAuthenticationProvider)
        .logout(c -> c
            .logoutUrl("/api/v1/admin/logout").permitAll()
            .logoutSuccessUrl(apiGatewayHost + LOGIN_ADDRESS)
        )
        .formLogin(configurer -> configurer
            .loginPage(LOGIN_ADDRESS).permitAll()
            .successHandler((request, response, authentication) ->
                response.sendRedirect(apiGatewayHost + "/api/v1/admin/all-services"))
            .failureHandler(simpleUrlAuthenticationFailureHandler)
        )
        .exceptionHandling(c -> c.authenticationEntryPoint(authenticationEntryPoint));
    return http.build();
  }

  @Bean
  public String apiGatewayHost(@Value("${microservices.host}") String host) {
    ServiceInstance serviceInstance = loadBalancerClient.choose("api-gateway");
    return String.format("http://%s:%s", host, serviceInstance.getPort());
  }

  @Bean
  public SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler(String apiGatewayHost) {
    return new SimpleUrlAuthenticationFailureHandler(apiGatewayHost + LOGIN_ADDRESS);
  }


  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder);
    provider.setUserDetailsService(userDetailsService);
    return provider;
  }
}
