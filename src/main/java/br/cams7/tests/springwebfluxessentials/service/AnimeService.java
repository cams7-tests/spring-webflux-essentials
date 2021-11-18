package br.cams7.tests.springwebfluxessentials.service;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnimeService {
  private final AnimeRepository repository;

  public Flux<Anime> findAll() {
    return repository.findAll();
  }

  public Mono<Anime> findById(Long id) {
    return repository.findById(id);
  }
}
