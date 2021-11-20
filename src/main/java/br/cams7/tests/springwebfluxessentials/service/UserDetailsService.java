package br.cams7.tests.springwebfluxessentials.service;

import br.cams7.tests.springwebfluxessentials.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements ReactiveUserDetailsService {
  private final UserRepository repository;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return repository.findByUsername(username).cast(UserDetails.class);
  }
}
