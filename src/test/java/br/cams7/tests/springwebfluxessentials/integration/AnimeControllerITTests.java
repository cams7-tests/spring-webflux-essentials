package br.cams7.tests.springwebfluxessentials.integration;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.exception.CustomAttributes;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
import br.cams7.tests.springwebfluxessentials.service.AnimeService;
import br.cams7.tests.springwebfluxessentials.utils.AnimeCreator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import({AnimeService.class, CustomAttributes.class})
// @SpringBootTest
// @AutoConfigureWebTestClient
public class AnimeControllerITTests {

  @MockBean private AnimeRepository repositoryMock;

  @Autowired private WebTestClient testClient;

  private static final Anime createdAnime = AnimeCreator.createValidAnime();
  private static final Anime secoundCreatedAnime = createdAnime.withId(2L).withName("Death Note");

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install(
        // builder -> builder.allowBlockingCallsInside("java.util.UUID", "randomUUID")
        );
  }

  @BeforeEach
  public void setUp() {
    BDDMockito.when(repositoryMock.findAll())
        .thenReturn(Flux.just(createdAnime, secoundCreatedAnime));
    BDDMockito.when(repositoryMock.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Mono.just(createdAnime));
    BDDMockito.when(repositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
        .thenReturn(Mono.just(createdAnime));
    BDDMockito.when(repositoryMock.saveAll(ArgumentMatchers.anySet()))
        .thenReturn(Flux.just(createdAnime, secoundCreatedAnime));
    BDDMockito.when(repositoryMock.delete(ArgumentMatchers.any(Anime.class)))
        .thenReturn(Mono.empty());
  }

  @Test
  public void blockHoundWorks() {
    var task =
        new FutureTask<>(
            () -> {
              Thread.sleep(0);
              return "";
            });
    Schedulers.parallel().schedule(task);

    try {
      task.get(10, TimeUnit.SECONDS);
      Assertions.fail("Should fail");
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
    }
  }

  @Test
  @DisplayName("listAll returns all animes when successfull")
  public void listAll_ReturnsAllAnimes_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.[0].id")
        .isEqualTo(createdAnime.getId())
        .jsonPath("$.[0].name")
        .isEqualTo(createdAnime.getName())
        .jsonPath("$.[1].id")
        .isEqualTo(secoundCreatedAnime.getId())
        .jsonPath("$.[1].name")
        .isEqualTo(secoundCreatedAnime.getName());
  }

  @Test
  @DisplayName("listAll returns all animes when successfull")
  public void listAll_Flavor2_ReturnsAllAnimes_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Anime.class)
        .hasSize(2)
        .contains(createdAnime, secoundCreatedAnime);
  }

  @Test
  @DisplayName("findById returns an anime when successfull")
  public void findById_ReturnsAnAnime_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(createdAnime.getId())
        .jsonPath("$.name")
        .isEqualTo(createdAnime.getName());
  }

  @Test
  @DisplayName("findById returns an anime when successfull")
  public void findById_Flavor2_ReturnsAnAnime_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Anime.class)
        .isEqualTo(createdAnime);
  }

  @Test
  @DisplayName("findById returns error when empty is returned")
  public void findById_ReturnsError_WhenEmptyIsReturned() {
    BDDMockito.when(repositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    testClient
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(404)
        .jsonPath("$.developerMessage")
        .isEqualTo("A ResponseStatusException happened");
  }

  @Test
  @DisplayName("save creates an anime when successfull")
  public void save_CreatesAnAnime_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(Anime.class)
        .isEqualTo(createdAnime);
  }

  @Test
  @DisplayName("save returns error when name is empty")
  public void save_ReturnsError_WhenNameIsEmpty() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");
    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(400);
  }

  @Test
  @DisplayName("saveBatch creates animes when successfull")
  public void saveBatch_CreatesAnimes_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromValue(Set.of(animeToBeSaved, animeToBeSaved.withName("Death Note"))))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBodyList(Anime.class)
        .hasSize(2)
        .contains(createdAnime, secoundCreatedAnime);
  }

  @Test
  @DisplayName("saveBatch returns error when one of the animes contains null or empty name")
  public void saveBatch_ReturnsError_WhenOneOfAnimesContainsNullOrEmptyName() {
    BDDMockito.when(repositoryMock.saveAll(ArgumentMatchers.anySet()))
        .thenReturn(Flux.just(createdAnime, secoundCreatedAnime.withName("")));
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(Set.of(animeToBeSaved, animeToBeSaved.withName(""))))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(400)
        .jsonPath("$.message")
        .isEqualTo("400 BAD_REQUEST \"Invalid name\"");
  }

  @Test
  @DisplayName("delete removes the anime when successfull")
  public void delete_RemovesTheAnime_WhenSuccessful() {
    testClient.delete().uri("/animes/{id}", 1).exchange().expectStatus().isNoContent();
  }

  @Test
  @DisplayName("delete returns error when empty is returned")
  public void delete_ReturnsError_WhenEmptyIsReturned() {
    BDDMockito.when(repositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    testClient
        .delete()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(404);
  }

  @Test
  @DisplayName("update saves updated anime when successfull")
  public void update_SavesUpdatedAnime_WhenSuccessful() {
    var updatedAnime = AnimeCreator.createValidUpdatedAnime();
    var animeToBeUpdated = AnimeCreator.createAnimeToBeSaved().withName(updatedAnime.getName());
    BDDMockito.when(repositoryMock.save(updatedAnime)).thenReturn(Mono.just(updatedAnime));
    testClient
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  @DisplayName("update returns error when name is empty")
  public void update_ReturnsError_WhenNameIsEmpty() {
    var animeToBeUpdated = AnimeCreator.createAnimeToBeSaved().withName("");
    testClient
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(400);
  }

  @Test
  @DisplayName("update returns error when empty is returned")
  public void update_ReturnsError_WhenEmptyIsReturned() {
    var updatedAnime = AnimeCreator.createValidUpdatedAnime();
    var animeToBeUpdated = AnimeCreator.createAnimeToBeSaved().withName(updatedAnime.getName());
    BDDMockito.when(repositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    testClient
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(404);
  }
}