package br.cams7.tests.springwebfluxessentials.repository;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AnimeRepository extends ReactiveCrudRepository<Anime, Long> {
  Mono<Anime> findById(Long id);
}
