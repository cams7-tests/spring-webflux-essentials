package br.cams7.tests.springwebfluxessentials.repository;

import br.cams7.tests.springwebfluxessentials.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
  Mono<User> findByUsername(String username);
}
