package dev.springwebflux.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.springwebflux.domain.Anime;
import dev.springwebflux.repository.AnimeRepository;
import dev.springwebflux.util.AnimeCreator;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
public class AnimeControllerIT {

  private static final String REGULAR_USER = "theo";
  private static final String ADMIN_USER = "wall";

  @Autowired
  WebTestClient client;

  @MockBean
  AnimeRepository animeRepository;


  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  static void blockHoundSetup() {
    BlockHound.install();
  }

  @Test()
  @DisplayName("SHOULD verify BlockHound setup on domain project")
  void verifyBlockHoundInstalled() {
    final Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                Mono.delay(Duration.ofMillis(1))
                    .doOnNext(
                        it -> {
                          try {
                            Thread.sleep(10);
                          } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                          }
                        })
                    .block()); // should throw an exception about Thread.sleep

    assertNotNull(exception.getMessage());
    assertTrue(exception.getMessage().contains("Blocking call!"));
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD returns Flux of anime when user is successfully authenticated and has role ADMIN")
  void shouldReturnsFluxOfAnimeWhenUserIsSuccessfullyAuthenticatedAndHaveTheRoleAdmin2() {
    Mockito.when(animeRepository.findAll()).thenReturn(Flux.just(anime));

    client
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Anime.class)
        .hasSize(1)
        .contains(anime);
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD returns Flux of anime when user is successfully authenticated and has role ADMIN")
  void shouldReturnsFluxOfAnimeWhenUserIsSuccessfullyAuthenticatedAndHaveTheRoleAdmin() {
    Mockito.when(animeRepository.findAll()).thenReturn(Flux.just(anime));

    client
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.[0].id").isEqualTo(anime.getId())
        .jsonPath("$.[0].name").isEqualTo(anime.getName());
  }

  @Test
  @DisplayName("SHOULD returns unauthorized when user is not authenticated")
  void shouldReturnsUnauthorizedWhenUserIsNotAuthenticated() {
    Mockito.when(animeRepository.findAll()).thenReturn(Flux.just(anime));

    client
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @WithUserDetails(REGULAR_USER)
  @DisplayName("SHOULD returns forbidden when user is successfully authenticated and does not have the role ADMIN")
  void shouldReturnsForbiddenWhenUserIsSuccessfullyAuthenticatedAndDoesNotHaveTheRoleAdmin() {

    Mockito.when(animeRepository.findAll()).thenReturn(Flux.just(anime));

    client
        .get()
        .uri("/animes")
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  @WithUserDetails(REGULAR_USER)
  @DisplayName("SHOULD returns Mono with anime when it exists and user is successfully authenticated and has role USER")
  void shouldReturnsMonoWithAnimeWhenItExists() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.just(anime));

    client
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @WithUserDetails(REGULAR_USER)
  @DisplayName("SHOULD THROW ERROR when anime does not exist and user is successfully authenticated and has role USER")
  void shouldThrowErrorWhenAnimeDoesNotExist() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.empty());

    client
        .get()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404)
        .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException happened");
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD create an anime when user is successfully authenticated and has role ADMIN")
  void shouldCreateAnAnime() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Mockito.when(animeRepository.save(Mockito.any(Anime.class))).thenReturn(Mono.just(anime));

    client
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Anime.class)
        .isEqualTo(anime);
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD throw error when trying create an anime with empty name and user is successfully authenticated and has role ADMIN")
  void shouldThrowErrorWhenTryingCreateAnAnimeWithEmptyName() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");
    Mockito.when(animeRepository.save(Mockito.any(Anime.class))).thenReturn(Mono.just(anime));

    client
        .post()
        .uri("/animes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(animeToBeSaved))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD save an anime list when user is successfully authenticated and has role ADMIN")
  void shouldSaveAnAnimeList() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Mockito.when(animeRepository.saveAll(Mockito.anyList())).thenReturn(Flux.just(anime, anime));

    client
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
        .exchange()
        .expectStatus().isCreated()
        .expectBodyList(Anime.class)
        .hasSize(2)
        .contains(anime);
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD throw error when one of the objects in the anime list to be saved contains a null or empty name and user is successfully authenticated and has role ADMIN")
  void shouldThrowErrorWhenOneOfTheObjectsInTheAnimeListToBeSavedContainsANullOrEmptyName() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Mockito.when(animeRepository.saveAll(Mockito.anyList())).thenReturn(Flux.just(anime, anime.withName("")));

    client
        .post()
        .uri("/animes/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved.withName(""))))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400)
        .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException happened");
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD delete an anime when user is successfully authenticated and has role ADMIN")
  void shouldDeleteAnAnime() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.just(anime));
    Mockito.when(animeRepository.delete(Mockito.any(Anime.class))).thenReturn(Mono.empty());

    client
        .delete()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD throw an error when trying delete an anime that does not exist and user is successfully authenticated and has role ADMIN")
  void shouldThrowAnErrorWhenTryingDeleteAnAnimeThatDoesNotExist() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.empty());

    client
        .delete()
        .uri("/animes/{id}", 1)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404)
        .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException happened");
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD update an anime when user is successfully authenticated and has role ADMIN")
  void shouldUpdateAnAnime() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.just(anime));
    Mockito.when(animeRepository.save(Mockito.any(Anime.class))).thenReturn(Mono.just(AnimeCreator.createValidUpdateAnime()));

    client
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(anime))
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @WithUserDetails(ADMIN_USER)
  @DisplayName("SHOULD throw an error when trying update an anime that does not exist and user is successfully authenticated and has role ADMIN")
  void shouldThrowAnErrorWhenTryingUpdateAnAnimeThatDoesNotExist() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.empty());

    client
        .put()
        .uri("/animes/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(anime))
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404)
        .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException happened");
  }
}
