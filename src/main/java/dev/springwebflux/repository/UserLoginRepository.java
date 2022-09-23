package dev.springwebflux.repository;

import dev.springwebflux.domain.UserLogin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserLoginRepository extends ReactiveCrudRepository<UserLogin, Integer> {
  Mono<UserLogin> findByUsername(String username);
}
