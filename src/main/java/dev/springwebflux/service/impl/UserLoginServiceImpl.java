package dev.springwebflux.service.impl;

import dev.springwebflux.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements ReactiveUserDetailsService {

  private final UserLoginRepository userLoginRepository;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return userLoginRepository.findByUsername(username)
        .cast(UserDetails.class);
  }
}
