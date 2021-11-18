package br.cams7.tests.springwebfluxessentials.controller;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("animes")
public class AnimeController {

  private final AnimeService service;

  @GetMapping
  public Flux<Anime> listAll() {
    return service.findAll();
  }

  @GetMapping(path = "{id}")
  public Mono<Anime> findById(@PathVariable Long id) {
    return service.findById(id);
  }
}
