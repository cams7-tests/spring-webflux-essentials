package br.cams7.tests.springwebfluxessentials.utils;

import br.cams7.tests.springwebfluxessentials.domain.Anime;

public class AnimeCreator {
  public static Anime createAnimeToBeSaved() {
    return Anime.builder().name("Naruto").build();
  }

  public static Anime createValidAnime() {
    return Anime.builder().id(1L).name("Naruto").build();
  }

  public static Anime createValidUpdatedAnime() {
    return Anime.builder().id(1L).name("The Seven Deadly Sins").build();
  }
}
