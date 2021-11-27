package br.cams7.tests.springwebfluxessentials.controller;

import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.DELETED_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.FIRST_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.SECOUND_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.UPDATED_ANIME_ID;
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
import br.cams7.tests.springwebfluxessentials.service.AnimeService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
class AnimeControllerTests {
  @InjectMocks private AnimeController controller;

  @Mock private AnimeService service;

  private static final Anime ANIME_TO_BE_SAVED = getAnimeToBeSaved();
  private static final Anime FIRST_ANIME = getFirstAnime();
  private static final Anime SECOUND_ANIME = getSecoundAnime();
  private static final Anime UPDATED_ANIME = getUpdatedAnime();

  @BeforeEach
  void setUp() {
    when(service.findAll()).thenReturn(Flux.just(FIRST_ANIME, SECOUND_ANIME));
    when(service.findById(anyLong())).thenReturn(Mono.just(FIRST_ANIME));
    when(service.save(any(Anime.class)))
        .thenReturn(Mono.just(ANIME_TO_BE_SAVED.withId(FIRST_ANIME_ID)));
    when(service.saveAll(anySet()))
        .thenReturn(
            Flux.just(
                ANIME_TO_BE_SAVED.withId(FIRST_ANIME_ID),
                ANIME_TO_BE_SAVED.withId(SECOUND_ANIME_ID).withName("Death Note")));
    when(service.delete(anyLong())).thenReturn(Mono.empty());
    when(service.update(any(Anime.class))).thenReturn(Mono.empty());
  }

  @Test
  @DisplayName("listAll returns all animes when successfull")
  void listAll_ReturnsAllAnimes_WhenSuccessful() {
    create(controller.listAll())
        .expectSubscription()
        .expectNext(FIRST_ANIME)
        .expectNext(SECOUND_ANIME)
        .verifyComplete();
  }

  @Test
  @DisplayName("getById returns an anime when successfull")
  void getById_ReturnsAnAnime_WhenSuccessful() {
    create(controller.getById(FIRST_ANIME_ID))
        .expectSubscription()
        .expectNext(FIRST_ANIME)
        .verifyComplete();
  }

  @Test
  @DisplayName("save creates an anime when successfull")
  void save_CreatesAnAnime_WhenSuccessful() {
    create(controller.save(ANIME_TO_BE_SAVED))
        .expectSubscription()
        .expectNext(ANIME_TO_BE_SAVED.withId(FIRST_ANIME_ID))
        .verifyComplete();
  }

  @Test
  @DisplayName("saveBatch creates animes when successfull")
  void saveBatch_CreatesAnimes_WhenSuccessful() {
    create(
            controller.saveBatch(
                Set.of(ANIME_TO_BE_SAVED, ANIME_TO_BE_SAVED.withName("Death Note"))))
        .expectSubscription()
        .expectNext(
            ANIME_TO_BE_SAVED.withId(FIRST_ANIME_ID),
            ANIME_TO_BE_SAVED.withId(SECOUND_ANIME_ID).withName("Death Note"))
        .verifyComplete();
  }

  @Test
  @DisplayName("delete removes the anime when successfull")
  void delete_RemovesTheAnime_WhenSuccessful() {
    create(controller.delete(DELETED_ANIME_ID)).expectSubscription().verifyComplete();
  }

  @Test
  @DisplayName("update saves updated anime when successfull")
  void update_SavesUpdatedAnime_WhenSuccessful() {
    create(controller.update(UPDATED_ANIME_ID, UPDATED_ANIME))
        .expectSubscription()
        .verifyComplete();
  }
}
