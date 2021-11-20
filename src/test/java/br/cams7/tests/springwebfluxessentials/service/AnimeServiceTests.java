package br.cams7.tests.springwebfluxessentials.service;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class AnimeServiceTests {

  @InjectMocks private AnimeService service;

  @Mock private AnimeRepository repository;

  private static final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  public void setUp() {
    BDDMockito.when(repository.findAll()).thenReturn(Flux.just(anime));
    BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.just(anime));
    BDDMockito.when(repository.save(AnimeCreator.createAnimeToBeSaved()))
        .thenReturn(Mono.just(anime));
    BDDMockito.when(repository.delete(ArgumentMatchers.any(Anime.class))).thenReturn(Mono.empty());
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
  @DisplayName("findAll returns a flux of anime when successfull")
  public void findAll_ReturnFluxOfAnime_WhenSuccessful() {
    StepVerifier.create(service.findAll()).expectSubscription().expectNext(anime).verifyComplete();
  }

  @Test
  @DisplayName("findById returns a mono with anime when it exists")
  public void findById_ReturnMonoWithAnime_WhenItExists() {
    StepVerifier.create(service.findById(1L))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns error when mono empty is returned")
  public void findById_ReturnError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    StepVerifier.create(service.findById(1L))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("save creates an anime when successfull")
  public void save_CreateAnAnime_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    StepVerifier.create(service.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("delete removes the anime when successfull")
  public void delete_RemoveTheAnime_WhenSuccessful() {
    StepVerifier.create(service.delete(1L)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("delete returns error when mono empty is returned")
  public void delete_ReturnError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    StepVerifier.create(service.delete(1L))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("update save updated anime when successfull")
  public void update_SaveUpdatedAnime_WhenSuccessful() {
    BDDMockito.when(repository.save(anime)).thenReturn(Mono.just(anime));
    StepVerifier.create(service.update(anime)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("update returns error when mono empty is returned")
  public void update_ReturnError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    StepVerifier.create(service.update(anime))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }
}
