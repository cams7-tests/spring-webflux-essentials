package br.cams7.tests.springwebfluxessentials.controller;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
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

  private static final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  public void setUp() {
    BDDMockito.when(service.findAll()).thenReturn(Flux.just(anime));
    BDDMockito.when(service.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.just(anime));
    BDDMockito.when(service.save(AnimeCreator.createAnimeToBeSaved())).thenReturn(Mono.just(anime));
    BDDMockito.when(service.delete(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    BDDMockito.when(service.update(AnimeCreator.createValidAnime())).thenReturn(Mono.empty());
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
  @DisplayName("findAll listAll a flux of anime when successfull")
  public void listAll_ReturnFluxOfAnime_WhenSuccessful() {
    StepVerifier.create(controller.listAll())
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns a mono with anime when it exists")
  public void findById_ReturnMonoWithAnime_WhenItExists() {
    StepVerifier.create(controller.findById(1L))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("save creates an anime when successfull")
  public void save_CreateAnAnime_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    StepVerifier.create(controller.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("delete removes the anime when successfull")
  public void delete_RemoveTheAnime_WhenSuccessful() {
    StepVerifier.create(controller.delete(1L)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("update save updated anime when successfull")
  public void update_SaveUpdatedAnime_WhenSuccessful() {
    StepVerifier.create(controller.update(1L, AnimeCreator.createValidAnime()))
        .expectSubscription()
        .verifyComplete();
  }
}
