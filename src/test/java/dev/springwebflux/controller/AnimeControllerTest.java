package dev.springwebflux.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.springwebflux.domain.Anime;
import dev.springwebflux.service.AnimeService;
import dev.springwebflux.service.impl.AnimeServiceImpl;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

  AnimeController animeController;
  AnimeService animeService;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  static void blockHoundSetup() {
    BlockHound.install();
  }

  @BeforeEach
  void setUp() {
    animeService = Mockito.mock(AnimeServiceImpl.class);
    animeController = new AnimeController(animeService);
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
    Mockito.when(animeService.findAll()).thenReturn(Flux.just(anime));

    StepVerifier.create(animeController.listAll())
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD returns Mono with anime when it exists")
  void shouldReturnsMonoWithAnimeWhenItExists() {
    Mockito.when(animeService.findById(Mockito.anyInt())).thenReturn(Mono.just(anime));

    StepVerifier.create(animeController.findById(1))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD save an anime")
  void shouldSaveAnAnime() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Mockito.when(animeService.save(Mockito.any(Anime.class))).thenReturn(Mono.just(anime));

    StepVerifier.create(animeController.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD save an anime list")
  void shouldSaveAnAnimeList() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
    Mockito.when(animeService.saveAll(Mockito.anyList())).thenReturn(Flux.just(anime, anime));

    StepVerifier.create(animeController.saveBatch(List.of(animeToBeSaved, animeToBeSaved)))
        .expectSubscription()
        .expectNext(anime, anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD delete an anime")
  void shouldDeleteAnAnime() {
    Mockito.when(animeService.delete(Mockito.anyInt())).thenReturn(Mono.empty());

    StepVerifier.create(animeController.delete(1))
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  @DisplayName("SHOULD update an anime")
  void shouldUpdateAnAnime() {
    Mockito.when(animeService.update(Mockito.any(Anime.class))).thenReturn(Mono.empty());

    StepVerifier.create(animeController.update(1, AnimeCreator.createValidUpdateAnime()))
        .expectSubscription()
        .verifyComplete();
  }

}