package br.cams7.tests.springwebfluxessentials.repository;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AnimeRepository extends ReactiveCrudRepository<Anime, Long> {
  Mono<Anime> findById(Long id);

  Flux<Anime> findAllBy(Pageable pageable);
}
