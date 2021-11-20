package br.cams7.tests.springwebfluxessentials.integration;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
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
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@ExtendWith(SpringExtension.class)
// @WebFluxTest
// @Import({AnimeService.class, CustomAttributes.class})
@SpringBootTest
@AutoConfigureWebTestClient
public class AnimeControllerITTests {

  private static final String USER = "user";
  private static final String ADMIN = "admin";

  @Autowired private WebTestClient testClient;

  @MockBean private AnimeRepository animeRepository;

  private static final Anime createdAnime = AnimeCreator.createValidAnime();
  private static final Anime secoundCreatedAnime = createdAnime.withId(2L).withName("Death Note");

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  public void setUp() {
    BDDMockito.when(animeRepository.findAll())
        .thenReturn(Flux.just(createdAnime, secoundCreatedAnime));
    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Mono.just(createdAnime));
    BDDMockito.when(animeRepository.save(AnimeCreator.createAnimeToBeSaved()))
        .thenReturn(Mono.just(createdAnime));
    BDDMockito.when(animeRepository.saveAll(ArgumentMatchers.anySet()))
        .thenReturn(Flux.just(createdAnime, secoundCreatedAnime));
    BDDMockito.when(animeRepository.delete(ArgumentMatchers.any(Anime.class)))
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
  @DisplayName(
      "listAll returns all animes when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
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
  @DisplayName(
      "listAll returns all animes when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
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
  @DisplayName("listAll returns unauthorized when user isn't authenticated")
  public void listAll_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient.get().uri("/animes").exchange().expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("findById returns an anime when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
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
  @DisplayName("findById returns an anime when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
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
  @DisplayName(
      "findById returns error when empty is returned and user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  public void findById_ReturnsError_WhenEmptyIsReturned() {
    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
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
  @DisplayName("findById returns unauthorized when user isn't authenticated")
  public void findById_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient.get().uri("/animes/{id}", 1).exchange().expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("save creates an anime when user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
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
  @DisplayName(
      "save returns error when name is empty and user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
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
  @DisplayName(
      "save returns forbidden when user is successfull authenticated and doesn't have role ADMIN")
  @WithUserDetails(USER)
  public void save_ReturnsForbidden_WhenUserDoesNotHaveRoleADMIN() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("save returns unauthorized when user isn't authenticated")
  public void save_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("saveBatch creates animes when user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
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
  @DisplayName(
      "saveBatch returns error when one of the animes contains null or empty name and user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  public void saveBatch_ReturnsError_WhenOneOfAnimesContainsNullOrEmptyName() {
    BDDMockito.when(animeRepository.saveAll(ArgumentMatchers.anySet()))
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
  @DisplayName(
      "saveBatch returns forbidden when user is successfull authenticated and doesn't have role ADMIN")
  @WithUserDetails(USER)
  public void saveBatch_ReturnsForbidden_WhenUserDoesNotHaveRoleADMIN() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(Set.of(animeToBeSaved, animeToBeSaved.withName(""))))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("saveBatch returns unauthorized when user isn't authenticated")
  public void saveBatch_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(Set.of(animeToBeSaved, animeToBeSaved.withName(""))))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("delete removes the anime when user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  public void delete_RemovesTheAnime_WhenSuccessful() {
    testClient.delete().uri("/animes/{id}", 1).exchange().expectStatus().isNoContent();
  }

  @Test
  @DisplayName(
      "delete returns error when empty is returned and user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  public void delete_ReturnsError_WhenEmptyIsReturned() {
    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
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
  @DisplayName(
      "delete returns forbidden when user is successfull authenticated and doesn't have role ADMIN")
  @WithUserDetails(USER)
  public void delete_ReturnsForbidden_WhenUserDoesNotHaveRoleADMIN() {
    testClient.delete().uri("/animes/{id}", 1).exchange().expectStatus().isForbidden();
  }

  @Test
  @DisplayName("delete returns unauthorized when user isn't authenticated")
  public void delete_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient.delete().uri("/animes/{id}", 1).exchange().expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName(
      "update saves updated anime when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  public void update_SavesUpdatedAnime_WhenSuccessful() {
    var updatedAnime = AnimeCreator.createValidUpdatedAnime();
    var animeToBeUpdated = AnimeCreator.createAnimeToBeSaved().withName(updatedAnime.getName());
    BDDMockito.when(animeRepository.save(updatedAnime)).thenReturn(Mono.just(updatedAnime));
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
  @DisplayName(
      "update returns error when name is empty and  user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
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
  @DisplayName(
      "update returns error when empty is returned and  user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  public void update_ReturnsError_WhenEmptyIsReturned() {
    var updatedAnime = AnimeCreator.createValidUpdatedAnime();
    var animeToBeUpdated = AnimeCreator.createAnimeToBeSaved().withName(updatedAnime.getName());
    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
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

  @Test
  @DisplayName("update returns unauthorized when user isn't authenticated")
  public void update_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    var updatedAnime = AnimeCreator.createValidUpdatedAnime();
    var animeToBeUpdated = AnimeCreator.createAnimeToBeSaved().withName(updatedAnime.getName());
    BDDMockito.when(animeRepository.save(updatedAnime)).thenReturn(Mono.just(updatedAnime));
    testClient
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeUpdated))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }
}
