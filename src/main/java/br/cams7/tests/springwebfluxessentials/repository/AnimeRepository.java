package br.cams7.tests.springwebfluxessentials.repository;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AnimeRepository extends ReactiveCrudRepository<Anime, Long> {}
