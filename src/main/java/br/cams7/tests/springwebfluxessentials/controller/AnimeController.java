package br.cams7.tests.springwebfluxessentials.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
@RequiredArgsConstructor
// @Slf4j
@RestController
@RequestMapping(value = "animes", produces = APPLICATION_JSON_VALUE)
@SecurityScheme(
    name = AnimeController.SECURITY_SCHEME_NAME,
    type = SecuritySchemeType.HTTP,
    scheme = AnimeController.SECURITY_SCHEME_SCHEME)
public class AnimeController {

  public static final String SECURITY_SCHEME_NAME = "Basic Authentication";
  public static final String SECURITY_SCHEME_SCHEME = "basic";
  public static final String OPERATION_TAGS = "anime";

  private final AnimeService service;

  @GetMapping("all")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "List all animes",
      tags = {OPERATION_TAGS},
      security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
  public Flux<Anime> listAll() {
    return service.findAll();
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "List animes",
      tags = {OPERATION_TAGS},
      security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
  public Mono<Page<Anime>> listByPage(
      @RequestParam("page") int page, @RequestParam("size") int size) {
    return service.findByPage(PageRequest.of(page, size));
  }

  @GetMapping(path = "{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get the anime by id",
      tags = {OPERATION_TAGS},
      security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
  public Mono<Anime> getById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create a new anime",
      tags = {OPERATION_TAGS},
      security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
  public Mono<Anime> save(@Valid @RequestBody Anime anime) {
    return service.save(anime);
  }

  @PostMapping("batch")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create new animes",
      tags = {OPERATION_TAGS},
      security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
  public Flux<Anime> saveBatch(
      @RequestBody @NotEmpty(message = "Input movie list cannot be empty.")
          Set<@Valid Anime> animes) {
    return service.saveAll(animes);
  }

  @PutMapping(path = "{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Update the anime by id",
      tags = {OPERATION_TAGS},
      security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
  public Mono<Void> update(@PathVariable Long id, @Valid @RequestBody Anime anime) {
    return service.update(anime.withId(id));
  }

  @DeleteMapping(path = "{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "Remove the anime by id",
      tags = {OPERATION_TAGS},
      security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
  public Mono<Void> delete(@PathVariable Long id) {
    return service.delete(id);
  }
}
