package br.cams7.tests.springwebfluxessentials.controller;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class AnimeControllerTests {
  @InjectMocks private AnimeController controller;

  @Mock private AnimeService service;

  private static final Anime createdAnime = AnimeCreator.createValidAnime();
  private static final Anime secoundCreatedAnime = createdAnime.withId(2L).withName("Death Note");

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  public void setUp() {
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
    StepVerifier.create(controller.listAll())
        .expectSubscription()
        .expectNext(createdAnime)
        .expectNext(secoundCreatedAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns an anime when successfull")
  public void findById_ReturnsAnAnime_WhenSuccessful() {
    StepVerifier.create(controller.findById(1L))
        .expectSubscription()
        .expectNext(createdAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("save creates an anime when successfull")
  public void save_CreatesAnAnime_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    StepVerifier.create(controller.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(createdAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("saveBatch creates animes when successfull")
  public void saveBatch_CreatesAnimes_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    StepVerifier.create(
            controller.saveBatch(Set.of(animeToBeSaved, animeToBeSaved.withName("Death Note"))))
        .expectSubscription()
        .expectNext(createdAnime, secoundCreatedAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("delete removes the anime when successfull")
  public void delete_RemovesTheAnime_WhenSuccessful() {
    StepVerifier.create(controller.delete(1L)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("update saves updated anime when successfull")
  public void update_SavesUpdatedAnime_WhenSuccessful() {
    var updatedAnime = AnimeCreator.createValidUpdatedAnime();
    var animeToBeUpdated = AnimeCreator.createAnimeToBeSaved().withName(updatedAnime.getName());
    StepVerifier.create(controller.update(1L, animeToBeUpdated))
        .expectSubscription()
        .verifyComplete();
  }
}
