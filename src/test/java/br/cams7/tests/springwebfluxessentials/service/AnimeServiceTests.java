package br.cams7.tests.springwebfluxessentials.service;

import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.DELETED_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.FIRST_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.SECOUND_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.getAnimeToBeSaved;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.getFirstAnime;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.getSecoundAnime;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.getUpdatedAnime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.when;
import static reactor.test.StepVerifier.create;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import br.cams7.tests.springwebfluxessentials.repository.AnimeRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class AnimeServiceTests {

  @InjectMocks private AnimeService service;

  @Mock private AnimeRepository repository;

  private static final Anime ANIME_TO_BE_SAVED = getAnimeToBeSaved();
  private static final Anime FIRST_ANIME = getFirstAnime();
  private static final Anime SECOUND_ANIME = getSecoundAnime();
  private static final Anime UPDATED_ANIME = getUpdatedAnime();

  @BeforeEach
  void setUp() {
    when(repository.findAll()).thenReturn(Flux.just(FIRST_ANIME, SECOUND_ANIME));
    when(repository.findById(anyLong())).thenReturn(Mono.just(FIRST_ANIME));
    when(repository.save(any(Anime.class)))
        .thenReturn(Mono.just(ANIME_TO_BE_SAVED.withId(FIRST_ANIME_ID)));
    when(repository.saveAll(anySet()))
        .thenReturn(
            Flux.just(
                ANIME_TO_BE_SAVED.withId(FIRST_ANIME_ID),
                ANIME_TO_BE_SAVED.withId(SECOUND_ANIME_ID).withName("Death Note")));
    when(repository.delete(any(Anime.class))).thenReturn(Mono.empty());
    when(repository.findAllBy(any(Pageable.class)))
        .thenReturn(Flux.just(FIRST_ANIME, SECOUND_ANIME));
    when(repository.count()).thenReturn(Mono.just(2l));
  }

  @Test
  @DisplayName("findAll returns all animes when successfull")
  void findAll_ReturnsAllAnimes_WhenSuccessful() {
    create(service.findAll())
        .expectSubscription()
        .expectNext(FIRST_ANIME)
        .expectNext(SECOUND_ANIME)
        .verifyComplete();
  }

  @Test
  @DisplayName("findByPageable returns all animes when successfull")
  void findByPageable_ReturnsAllAnimes_WhenSuccessful() {
    create(service.findByPageable(PageRequest.of(0, 1)))
        .expectSubscription()
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns an anime when successfull")
  void findById_ReturnsAnAnime_WhenSuccessful() {
    create(service.findById(FIRST_ANIME_ID))
        .expectSubscription()
        .expectNext(FIRST_ANIME)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns error when empty is returned")
  void findById_ReturnsError_WhenEmptyIsReturned() {
    when(repository.findById(anyLong())).thenReturn(Mono.empty());
    create(service.findById(FIRST_ANIME_ID))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("save creates an anime when successfull")
  void save_CreatesAnAnime_WhenSuccessful() {
    create(service.save(ANIME_TO_BE_SAVED))
        .expectSubscription()
        .expectNext(ANIME_TO_BE_SAVED.withId(FIRST_ANIME_ID))
        .verifyComplete();
  }

  @Test
  @DisplayName("saveAll creates animes when successfull")
  void saveAll_CreatesAnimes_WhenSuccessful() {
    create(service.saveAll(Set.of(ANIME_TO_BE_SAVED, ANIME_TO_BE_SAVED.withName("Death Note"))))
        .expectSubscription()
        .expectNext(
            ANIME_TO_BE_SAVED.withId(FIRST_ANIME_ID),
            ANIME_TO_BE_SAVED.withId(SECOUND_ANIME_ID).withName("Death Note"))
        .verifyComplete();
  }

  @Test
  @DisplayName("delete removes the anime when successfull")
  void delete_RemovesTheAnime_WhenSuccessful() {
    create(service.delete(DELETED_ANIME_ID)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("delete returns error when empty is returned")
  void delete_ReturnsError_WhenEmptyIsReturned() {
    when(repository.findById(anyLong())).thenReturn(Mono.empty());
    create(service.delete(DELETED_ANIME_ID))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("update saves updated anime when successfull")
  void update_SavesUpdatedAnime_WhenSuccessful() {
    when(repository.save(any(Anime.class))).thenReturn(Mono.just(UPDATED_ANIME));
    StepVerifier.create(service.update(UPDATED_ANIME)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("update returns error when empty is returned")
  void update_ReturnsError_WhenEmptyIsReturned() {
    when(repository.findById(anyLong())).thenReturn(Mono.empty());
    StepVerifier.create(service.update(UPDATED_ANIME))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }
}
