package br.cams7.tests.springwebfluxessentials.service;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnimeService {
  private final AnimeRepository repository;

  public Flux<Anime> findAll() {
    return repository.findAll(); // .log();
  }

  public Mono<Anime> findById(Long id) {
    return repository.findById(id).switchIfEmpty(monoResponseStatusNotFoundException()); // .log();
  }

  private static <T> Mono<T> monoResponseStatusNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
}
