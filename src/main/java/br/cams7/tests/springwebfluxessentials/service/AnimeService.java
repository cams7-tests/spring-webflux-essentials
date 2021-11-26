package br.cams7.tests.springwebfluxessentials.service;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
import io.netty.util.internal.StringUtil;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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
    return repository.findById(id).switchIfEmpty(monoResponseStatusNotFoundException());
  }

  public Mono<Anime> save(Anime anime) {
    return repository.save(anime);
  }

  @Transactional
  public Flux<Anime> saveAll(Set<Anime> animes) {
    return repository
        .saveAll(animes)
        .doOnNext(AnimeService::throwResponseStatusExceptionWhenEmptyName);
  }

  public Mono<Void> update(Anime anime) {
    return findById(anime.getId())
        .map(animeFound -> anime.withId(animeFound.getId()))
        .flatMap(repository::save)
        .then();
  }

  public Mono<Void> delete(Long id) {
    return findById(id).flatMap(repository::delete);
  }

  private static <T> Mono<T> monoResponseStatusNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  private static void throwResponseStatusExceptionWhenEmptyName(Anime anime) {
    if (StringUtil.isNullOrEmpty(anime.getName()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
  }
}
