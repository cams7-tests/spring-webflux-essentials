package br.cams7.tests.springwebfluxessentials.integration;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.exception.CustomAttributes;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
import br.cams7.tests.springwebfluxessentials.service.AnimeService;
import br.cams7.tests.springwebfluxessentials.utils.AnimeCreator;
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
public class AnimeControllerITTests {

  @MockBean private AnimeRepository repositoryMock;

  @Autowired private WebTestClient testClient;

  private static final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  public void setUp() {
    BDDMockito.when(repositoryMock.findAll()).thenReturn(Flux.just(anime));
    BDDMockito.when(repositoryMock.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Mono.just(anime));
    BDDMockito.when(repositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
        .thenReturn(Mono.just(anime));
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
  @DisplayName("listAll returns a flux of anime when successfull")
  public void listAll_ReturnFluxOfAnime_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.[0].id")
        .isEqualTo(anime.getId())
        .jsonPath("$.[0].name")
        .isEqualTo(anime.getName());
  }

  @Test
  @DisplayName("listAll returns a flux of anime when successfull")
  public void listAll_Flavor2_ReturnFluxOfAnime_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Anime.class)
        .hasSize(1)
        .contains(anime);
  }

  @Test
  @DisplayName("findById returns a mono with anime when it exists")
  public void findById_ReturnMonoWithAnime_WhenItExists() {
    testClient
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(anime.getId())
        .jsonPath("$.name")
        .isEqualTo(anime.getName());
  }

  @Test
  @DisplayName("findById returns a mono with anime when it exists")
  public void findById_Flavor2_ReturnMonoWithAnime_WhenItExists() {
    testClient
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("findById returns error when mono empty is returned")
  public void findById_ReturnError_WhenEmptyMonoIsReturned() {
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
  public void save_CreateAnAnime_WhenSuccessful() {
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
        .isEqualTo(anime);
  }

  @Test
  @DisplayName("save returns error when name is empty")
  public void save_ReturnError_WhenNameIsEmpty() {
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
  @DisplayName("delete removes the anime when successfull")
  public void delete_RemoveTheAnime_WhenSuccessful() {
    testClient.delete().uri("/animes/{id}", 1).exchange().expectStatus().isNoContent();
  }

  @Test
  @DisplayName("delete returns error when mono empty is returned")
  public void delete_ReturnError_WhenEmptyMonoIsReturned() {
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
  @DisplayName("update save updated anime when successfull")
  public void update_SaveUpdatedAnime_WhenSuccessful() {
    BDDMockito.when(repositoryMock.save(anime)).thenReturn(Mono.just(anime));
    testClient
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(anime))
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  @DisplayName("update returns error when mono empty is returned")
  public void update_ReturnError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(repositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    testClient
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(anime))
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(404);
  }
}
