package br.cams7.tests.springwebfluxessentials.service;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class AnimeServiceTests {

  @InjectMocks private AnimeService service;

  @Mock private AnimeRepository repository;

  private static final Anime createdAnime = AnimeCreator.createValidAnime();
  private static final Anime secoundCreatedAnime = createdAnime.withId(2L).withName("Death Note");

  @BeforeEach
  void setUp() {
    BDDMockito.when(repository.findAll()).thenReturn(Flux.just(createdAnime, secoundCreatedAnime));
    BDDMockito.when(repository.findById(ArgumentMatchers.anyLong()))
        .thenReturn(Mono.just(createdAnime));
    BDDMockito.when(repository.save(AnimeCreator.createAnimeToBeSaved()))
        .thenReturn(Mono.just(createdAnime));
    BDDMockito.when(repository.saveAll(ArgumentMatchers.anySet()))
        .thenReturn(Flux.just(createdAnime, secoundCreatedAnime));
    BDDMockito.when(repository.delete(ArgumentMatchers.any(Anime.class))).thenReturn(Mono.empty());
  }

  @Test
  @DisplayName("findAll returns all animes when successfull")
  void findAll_ReturnsAllAnimes_WhenSuccessful() {
    StepVerifier.create(service.findAll())
        .expectSubscription()
        .expectNext(createdAnime)
        .expectNext(secoundCreatedAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns an anime when successfull")
  void findById_ReturnsAnAnime_WhenSuccessful() {
    StepVerifier.create(service.findById(1L))
        .expectSubscription()
        .expectNext(createdAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns error when empty is returned")
  void findById_ReturnsError_WhenEmptyIsReturned() {
    BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    StepVerifier.create(service.findById(1L))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("save creates an anime when successfull")
  void save_CreatesAnAnime_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    StepVerifier.create(service.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(createdAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("saveAll creates animes when successfull")
  void saveAll_CreatesAnimes_WhenSuccessful() {
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    StepVerifier.create(
            service.saveAll(Set.of(animeToBeSaved, animeToBeSaved.withName("Death Note"))))
        .expectSubscription()
        .expectNext(createdAnime, secoundCreatedAnime)
        .verifyComplete();
  }

  @Test
  @DisplayName("saveAll returns error when one of the animes contains null or empty name")
  void saveAll_ReturnsError_WhenOneOfAnimesContainsNullOrEmptyName() {
    BDDMockito.when(repository.saveAll(ArgumentMatchers.anySet()))
        .thenReturn(Flux.just(createdAnime, secoundCreatedAnime.withName("")));
    var animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    StepVerifier.create(service.saveAll(Set.of(animeToBeSaved, animeToBeSaved.withName(""))))
        .expectSubscription()
        .expectNext(createdAnime)
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("delete removes the anime when successfull")
  void delete_RemovesTheAnime_WhenSuccessful() {
    StepVerifier.create(service.delete(1L)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("delete returns error when empty is returned")
  void delete_ReturnsError_WhenEmptyIsReturned() {
    BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    StepVerifier.create(service.delete(1L))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("update saves updated anime when successfull")
  void update_SavesUpdatedAnime_WhenSuccessful() {
    var updatedAnime = AnimeCreator.createValidUpdatedAnime();
    BDDMockito.when(repository.save(updatedAnime)).thenReturn(Mono.just(updatedAnime));
    StepVerifier.create(service.update(updatedAnime)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("update returns error when empty is returned")
  void update_ReturnsError_WhenEmptyIsReturned() {
    var updatedAnime = AnimeCreator.createValidUpdatedAnime();
    BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Mono.empty());
    StepVerifier.create(service.update(updatedAnime))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }
}
