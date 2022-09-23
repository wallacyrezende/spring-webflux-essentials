package dev.springwebflux.config;

import dev.springwebflux.service.impl.UserLoginServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  private final String ADMIN = "ADMIN";
  private final String USER = "USER";

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        .csrf().disable()
        .authorizeExchange()
          .pathMatchers(HttpMethod.POST, "/animes/**").hasRole(ADMIN)
          .pathMatchers(HttpMethod.PUT, "/animes/**").hasRole(ADMIN)
          .pathMatchers(HttpMethod.DELETE, "/animes/**").hasRole(ADMIN)
          .pathMatchers(HttpMethod.GET, "/animes/**").hasRole(USER)
          .pathMatchers(HttpMethod.GET, "/webjars/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
        .anyExchange().authenticated()
        .and().formLogin()
        .and().httpBasic()
        .and().build();
  }

  @Bean
  ReactiveAuthenticationManager authenticationManager(UserLoginServiceImpl userService) {
    return new UserDetailsRepositoryReactiveAuthenticationManager(userService);
  }

}
