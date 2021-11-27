package br.cams7.tests.springwebfluxessentials.integration;

import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.DELETED_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.FIRST_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.INVALID_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.UPDATED_ANIME_ID;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.getAnimeToBeSaved;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.getFirstAnime;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.getSecoundAnime;
import static br.cams7.tests.springwebfluxessentials.utils.AnimeCreator.getUpdatedAnime;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import br.cams7.tests.springwebfluxessentials.domain.Anime;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class AnimeControllerITests {

  private static final String USER = "user";
  private static final String ADMIN = "admin";

  @Autowired private WebTestClient testClient;

  private static final Anime ANIME_TO_BE_SAVED = getAnimeToBeSaved();
  private static final Anime FIRST_ANIME = getFirstAnime();
  private static final Anime SECOUND_ANIME = getSecoundAnime();
  private static final Anime UPDATED_ANIME = getUpdatedAnime();

  @Test
  @DisplayName(
      "listAll returns all animes when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  void listAll_ReturnsAllAnimes_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes/all")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.[0].id")
        .isEqualTo(FIRST_ANIME.getId())
        .jsonPath("$.[0].name")
        .isEqualTo(FIRST_ANIME.getName())
        .jsonPath("$.[1].id")
        .isEqualTo(SECOUND_ANIME.getId())
        .jsonPath("$.[1].name")
        .isEqualTo(SECOUND_ANIME.getName());
  }

  @Test
  @DisplayName(
      "listAll returns all animes when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  void listAll_Flavor2_ReturnsAllAnimes_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes/all")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Anime.class)
        .contains(FIRST_ANIME, SECOUND_ANIME);
  }

  @Test
  @DisplayName("listAll returns unauthorized when user isn't authenticated")
  void listAll_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient.get().uri("/animes/all").exchange().expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName(
      "listByPageable returns all animes when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  void listByPageable_ReturnsAllAnimes_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes?page=0&size=3")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.content[0].name")
        .isEqualTo(FIRST_ANIME.getName())
        .jsonPath("$.content[0].publicationYear")
        .isEqualTo((int) FIRST_ANIME.getPublicationYear())
        .jsonPath("$.content[1].name")
        .isEqualTo(SECOUND_ANIME.getName())
        .jsonPath("$.content[1].publicationYear")
        .isEqualTo((int) SECOUND_ANIME.getPublicationYear())
        .jsonPath("$.pageable.pageNumber")
        .isEqualTo(0)
        .jsonPath("$.pageable.pageSize")
        .isEqualTo(3)
        .jsonPath("$.last")
        .isEqualTo(false)
        .jsonPath("$.number")
        .isEqualTo(0)
        .jsonPath("$.first")
        .isEqualTo(true)
        .jsonPath("$.numberOfElements")
        .isEqualTo(3)
        .jsonPath("$.size")
        .isEqualTo(3)
        .jsonPath("$.empty")
        .isEqualTo(false);
  }

  @Test
  @DisplayName("listByPageable returns unauthorized when user isn't authenticated")
  void listByPageable_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient.get().uri("/animes?page=0&size=3").exchange().expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("getById returns an anime when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  void getById_ReturnsAnAnime_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes/{id}", FIRST_ANIME_ID)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo(FIRST_ANIME.getId())
        .jsonPath("$.name")
        .isEqualTo(FIRST_ANIME.getName());
  }

  @Test
  @DisplayName("getById returns an anime when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  void getById_Flavor2_ReturnsAnAnime_WhenSuccessful() {
    testClient
        .get()
        .uri("/animes/{id}", FIRST_ANIME_ID)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Anime.class)
        .isEqualTo(FIRST_ANIME);
  }

  @Test
  @DisplayName(
      "getById returns error when empty is returned and user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  void getById_ReturnsError_WhenEmptyIsReturned() {
    testClient
        .get()
        .uri("/animes/{id}", INVALID_ANIME_ID)
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
  @DisplayName("getById returns unauthorized when user isn't authenticated")
  void getById_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient.get().uri("/animes/{id}", FIRST_ANIME_ID).exchange().expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("save creates an anime when user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  void save_CreatesAnAnime_WhenSuccessful() {
    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(ANIME_TO_BE_SAVED))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo(ANIME_TO_BE_SAVED.getName());
  }

  @Test
  @DisplayName(
      "save returns error when name is empty and user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  void save_ReturnsError_WhenNameIsEmpty() {
    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(ANIME_TO_BE_SAVED.withName("")))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(400)
        .jsonPath("$.developerMessage")
        .isEqualTo("A ResponseStatusException happened");
  }

  @Test
  @DisplayName(
      "save returns forbidden when user is successfull authenticated and doesn't have role ADMIN")
  @WithUserDetails(USER)
  void save_ReturnsForbidden_WhenUserDoesNotHaveRoleADMIN() {
    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(ANIME_TO_BE_SAVED))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("save returns unauthorized when user isn't authenticated")
  void save_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(ANIME_TO_BE_SAVED))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("saveBatch creates animes when user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  void saveBatch_CreatesAnimes_WhenSuccessful() {
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromValue(
                Set.of(
                    ANIME_TO_BE_SAVED.withName("Death Note"),
                    ANIME_TO_BE_SAVED.withName("One-Punch Man"))))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBodyList(Anime.class)
        .hasSize(2);
  }

  @Test
  @DisplayName(
      "saveBatch returns error when empty animes and user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  void saveBatch_ReturnsError_WhenEmptyAnimes() {
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(Set.of()))
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(500)
        .jsonPath("$.message")
        .isEqualTo("saveBatch.animes: Input movie list cannot be empty.")
        .jsonPath("$.developerMessage")
        .isEqualTo("A ConstraintViolationException happened");
  }

  @Test
  @DisplayName(
      "saveBatch returns error when one of the animes contains null or empty name and user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  void saveBatch_ReturnsError_WhenOneOfAnimesContainsNullOrEmptyName() {
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromValue(
                Set.of(ANIME_TO_BE_SAVED.withName("Death Note"), ANIME_TO_BE_SAVED.withName(""))))
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(500)
        .jsonPath("$.message")
        .isEqualTo("saveBatch.animes[].name: The name of this anime cannot be empty")
        .jsonPath("$.developerMessage")
        .isEqualTo("A ConstraintViolationException happened");
  }

  @Test
  @DisplayName(
      "saveBatch returns error when one of the animes is duplicated and user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  void saveBatch_ReturnsError_WhenOneOfAnimesIsDuplicated() {
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromValue(
                Set.of(
                    ANIME_TO_BE_SAVED.withName("Death Note"),
                    ANIME_TO_BE_SAVED.withName("Naruto"))))
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(500)
        .jsonPath("$.developerMessage")
        .isEqualTo("A DataIntegrityViolationException happened");
  }

  @Test
  @DisplayName(
      "saveBatch returns forbidden when user is successfull authenticated and doesn't have role ADMIN")
  @WithUserDetails(USER)
  void saveBatch_ReturnsForbidden_WhenUserDoesNotHaveRoleADMIN() {
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromValue(
                Set.of(
                    ANIME_TO_BE_SAVED.withName("Death Note"),
                    ANIME_TO_BE_SAVED.withName("One-Punch Man"))))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  @DisplayName("saveBatch returns unauthorized when user isn't authenticated")
  void saveBatch_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            BodyInserters.fromValue(
                Set.of(
                    ANIME_TO_BE_SAVED.withName("Death Note"),
                    ANIME_TO_BE_SAVED.withName("One-Punch Man"))))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("delete removes the anime when user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  void delete_RemovesTheAnime_WhenSuccessful() {
    testClient
        .delete()
        .uri("/animes/{id}", DELETED_ANIME_ID)
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  @DisplayName(
      "delete returns error when empty is returned and user is successfull authenticated and has role ADMIN")
  @WithUserDetails(ADMIN)
  void delete_ReturnsError_WhenEmptyIsReturned() {
    testClient
        .delete()
        .uri("/animes/{id}", INVALID_ANIME_ID)
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
  @DisplayName(
      "delete returns forbidden when user is successfull authenticated and doesn't have role ADMIN")
  @WithUserDetails(USER)
  void delete_ReturnsForbidden_WhenUserDoesNotHaveRoleADMIN() {
    testClient.delete().uri("/animes/{id}", FIRST_ANIME_ID).exchange().expectStatus().isForbidden();
  }

  @Test
  @DisplayName("delete returns unauthorized when user isn't authenticated")
  void delete_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient
        .delete()
        .uri("/animes/{id}", FIRST_ANIME_ID)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName(
      "update saves updated anime when user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  void update_SavesUpdatedAnime_WhenSuccessful() {
    testClient
        .put()
        .uri("/animes/{id}", UPDATED_ANIME_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(UPDATED_ANIME))
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  @Test
  @DisplayName(
      "update returns error when name is empty and  user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  void update_ReturnsError_WhenNameIsEmpty() {
    testClient
        .put()
        .uri("/animes/{id}", UPDATED_ANIME_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(UPDATED_ANIME.withName("")))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo(400)
        .jsonPath("$.developerMessage")
        .isEqualTo("A ResponseStatusException happened");
  }

  @Test
  @DisplayName(
      "update returns error when empty is returned and  user is successfull authenticated and has role USER")
  @WithUserDetails(USER)
  void update_ReturnsError_WhenEmptyIsReturned() {
    testClient
        .put()
        .uri("/animes/{id}", INVALID_ANIME_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(UPDATED_ANIME))
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
  @DisplayName("update returns unauthorized when user isn't authenticated")
  void update_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
    testClient
        .put()
        .uri("/animes/{id}", UPDATED_ANIME_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(UPDATED_ANIME))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }
}
