package dev.springwebflux.util;

import dev.springwebflux.domain.Anime;

public class AnimeCreator {

  private static final String NARUTO = "Naruto";
  private static final String NARUTO_TEMP2 = "Naruto 2";

  public static Anime createAnimeToBeSaved() {
    return Anime.builder()
        .name(NARUTO)
        .build();
  }

  public static Anime createValidAnime() {
    return Anime.builder()
        .id(1)
        .name(NARUTO)
        .build();
  }

  public static Anime createValidUpdateAnime() {
    return Anime.builder()
        .id(1)
        .name(NARUTO_TEMP2)
        .build();
  }

}
