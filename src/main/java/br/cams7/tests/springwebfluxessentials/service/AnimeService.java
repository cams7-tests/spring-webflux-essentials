package br.cams7.tests.springwebfluxessentials.service;

import static br.cams7.tests.springwebfluxessentials.utils.CommonExceptions.responseNotFoundException;
import static java.time.temporal.ChronoUnit.MILLIS;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.logging.LogEntryExit;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnimeService {
  private final AnimeRepository repository;

  @LogEntryExit(unit = MILLIS)
  public Flux<Anime> findAll() {
    return repository.findAll();
  }

  @LogEntryExit(showArgs = true, unit = MILLIS)
  public Mono<Anime> findById(Long id) {
    return repository.findById(id).switchIfEmpty(responseNotFoundException());
  }

  @LogEntryExit(showArgs = true, unit = MILLIS)
  public Mono<Anime> save(Anime anime) {
    return repository.save(anime);
  }

  @LogEntryExit(showArgs = true, unit = MILLIS)
  @Transactional
  public Flux<Anime> saveAll(Set<Anime> animes) {
    return repository.saveAll(animes);
  }

  @LogEntryExit(showArgs = true, unit = MILLIS)
  public Mono<Void> update(Anime anime) {
    return findById(anime.getId())
        .map(animeFound -> anime.withId(animeFound.getId()))
        .flatMap(repository::save)
        .then();
  }

  @LogEntryExit(showArgs = true, unit = MILLIS)
  public Mono<Void> delete(Long id) {
    return findById(id).flatMap(repository::delete);
  }
}
