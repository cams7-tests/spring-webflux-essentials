package br.cams7.tests.springwebfluxessentials.controller;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.service.AnimeService;
import br.cams7.tests.springwebfluxessentials.utils.AnimeCreator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class AnimeControllerTests {
  @InjectMocks private AnimeController controller;

  @Mock private AnimeService service;

  private static final Anime createdAnime = AnimeCreator.createValidAnime();
  private static final Anime secoundCreatedAnime = createdAnime.withId(2L).withName("Death Note");

  // @BeforeAll
  // static void blockHoundSetup() {
  //  BlockHound.install();
  // }

  @BeforeEach
  void setUp() {
    BDDMockito.when(service.findAll()).thenReturn(Flux.just(createdAnime, secoundCreatedAnime));
    BDDMockito.when(service.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Mono.just(createdAnime));
    BDDMockito.when(service.save(AnimeCreator.createAnimeToBeSaved()))
        .thenReturn(Mono.just(createdAnime));
    BDDMockito.when(service.saveAll(ArgumentMatchers.anySet()))
        .thenReturn(Flux.just(createdAnime, secoundCreatedAnime));
    BDDMockito.when(service.delete(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    BDDMockito.when(service.update(AnimeCreator.createValidUpdatedAnime()))
        .thenReturn(Mono.empty());
  }

  /*@Test
  void blockHoundWorks() {
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
  }*/

  @Test
  @DisplayName("listAll returns all animes when successfull")
  void listAll_ReturnsAllAnimes_WhenSuccessful() {
    StepVerifier.create(controller.listAll())
        .expectSubscription()
        .expectNext(createdAnime)
        .expectNext(secoundCreatedAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns an anime when successfull")
  void findById_ReturnsAnAnime_WhenSuccessful() {
    StepVerifier.create(controller.findById(1L))
        .expectSubscription()
        .expectNext(createdAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("save creates an anime when successfull")
  void save_CreatesAnAnime_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    StepVerifier.create(controller.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(createdAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("saveBatch creates animes when successfull")
  void saveBatch_CreatesAnimes_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    StepVerifier.create(
            controller.saveBatch(Set.of(animeToBeSaved, animeToBeSaved.withName("Death Note"))))
        .expectSubscription()
        .expectNext(createdAnime, secoundCreatedAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("delete removes the anime when successfull")
  void delete_RemovesTheAnime_WhenSuccessful() {
    StepVerifier.create(controller.delete(1L)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("update saves updated anime when successfull")
  void update_SavesUpdatedAnime_WhenSuccessful() {
    var updatedAnime = AnimeCreator.createValidUpdatedAnime();
    var animeToBeUpdated = AnimeCreator.createAnimeToBeSaved().withName(updatedAnime.getName());
    StepVerifier.create(controller.update(1L, animeToBeUpdated))
        .expectSubscription()
        .verifyComplete();
  }
}
