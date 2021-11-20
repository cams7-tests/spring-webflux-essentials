package br.cams7.tests.springwebfluxessentials.controller;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.service.AnimeService;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
  @ResponseStatus(HttpStatus.OK)
  public Flux<Anime> listAll() {
    return service.findAll();
  }

  @GetMapping(path = "{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Anime> findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Anime> save(@Valid @RequestBody Anime anime) {
    return service.save(anime);
  }

  @PostMapping("batch")
  @ResponseStatus(HttpStatus.CREATED)
  public Flux<Anime> saveBatch(@RequestBody Set<Anime> animes) {
    return service.saveAll(animes);
  }

  @PutMapping(path = "{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> update(@PathVariable Long id, @Valid @RequestBody Anime anime) {
    return service.update(anime.withId(id));
  }

  @DeleteMapping(path = "{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> delete(@PathVariable Long id) {
    return service.delete(id);
  }
}
