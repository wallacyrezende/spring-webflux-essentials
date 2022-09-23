package dev.springwebflux.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.springwebflux.domain.Anime;
import dev.springwebflux.repository.AnimeRepository;
import dev.springwebflux.service.AnimeService;
import dev.springwebflux.util.AnimeCreator;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class AnimeServiceImplTest {

  AnimeService animeService;
  AnimeRepository animeRepository;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  void setUp() {
    this.animeRepository = Mockito.mock(AnimeRepository.class);
    animeService = new AnimeServiceImpl(animeRepository);
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
  @DisplayName("SHOULD returns Flux of anime")
  void shouldReturnsFluxOfAnime() {
    Mockito.when(animeRepository.findAll()).thenReturn(Flux.just(anime));

    StepVerifier.create(animeService.findAll())
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD returns Mono with anime when it exists")
  void shouldReturnsMonoWithAnimeWhenItExists() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.just(anime));

    StepVerifier.create(animeService.findById(1))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD THROW ERROR when anime does not exist")
  void shouldThrowErrorWhenAnimeDoesNotExist() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.empty());

    StepVerifier.create(animeService.findById(1))
        .expectSubscription()
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(ResponseStatusException.class);
          ResponseStatusException exception = (ResponseStatusException) throwable;
          assertEquals("404 NOT_FOUND \"Anime not found\"", exception.getMessage());
          assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        })
        .verify();
  }

  @Test
  @DisplayName("SHOULD save an anime")
  void shouldSaveAnAnime() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Mockito.when(animeRepository.save(Mockito.any(Anime.class))).thenReturn(Mono.just(anime));

    StepVerifier.create(animeService.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD save an anime list")
  void shouldSaveAnAnimeList() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Mockito.when(animeRepository.saveAll(Mockito.anyList())).thenReturn(Flux.just(anime, anime));

    StepVerifier.create(animeService.saveAll(List.of(animeToBeSaved, animeToBeSaved)))
        .expectSubscription()
        .expectNext(anime, anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD throw error when one of the objects in the anime list to be saved contains a null or empty name")
  void shouldThrowErrorWhenOneOfTheObjectsInTheAnimeListToBeSavedContainsANullOrEmptyName() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Mockito.when(animeRepository.saveAll(Mockito.anyList())).thenReturn(Flux.just(anime, anime.withName("")));

    StepVerifier.create(animeService.saveAll(List.of(animeToBeSaved, animeToBeSaved.withName(""))))
        .expectSubscription()
        .expectNext(anime)
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("SHOULD delete an anime")
  void shouldDeleteAnAnime() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.just(anime));
    Mockito.when(animeRepository.delete(Mockito.any(Anime.class))).thenReturn(Mono.empty());

    StepVerifier.create(animeService.delete(1))
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD throw an error when trying delete an anime that does not exist")
  void shouldThrowAnErrorWhenTryingDeleteAnAnimeThatDoesNotExist() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.empty());

    StepVerifier.create(animeService.delete(1))
        .expectSubscription()
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(ResponseStatusException.class);
          ResponseStatusException exception = (ResponseStatusException) throwable;
          assertEquals("404 NOT_FOUND \"Anime not found\"", exception.getMessage());
          assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        })
        .verify();
  }

  @Test
  @DisplayName("SHOULD update an anime")
  void shouldUpdateAnAnime() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.just(anime));
    Mockito.when(animeRepository.save(Mockito.any(Anime.class))).thenReturn(Mono.just(AnimeCreator.createValidUpdateAnime()));

    StepVerifier.create(animeService.update(AnimeCreator.createValidUpdateAnime()))
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD throw an error when trying update an anime that does not exist")
  void shouldThrowAnErrorWhenTryingUpdateAnAnimeThatDoesNotExist() {
    Mockito.when(animeRepository.findById(Mockito.anyInt())).thenReturn(Mono.empty());

    StepVerifier.create(animeService.update(AnimeCreator.createValidUpdateAnime()))
        .expectSubscription()
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(ResponseStatusException.class);
          ResponseStatusException exception = (ResponseStatusException) throwable;
          assertEquals("404 NOT_FOUND \"Anime not found\"", exception.getMessage());
          assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        })
        .verify();
  }

}