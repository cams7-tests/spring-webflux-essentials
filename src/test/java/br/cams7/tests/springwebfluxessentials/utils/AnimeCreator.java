package br.cams7.tests.springwebfluxessentials.utils;

import br.cams7.tests.springwebfluxessentials.domain.Anime;

public class AnimeCreator {

  public static long FIRST_ANIME_ID = 1l;
  public static long SECOUND_ANIME_ID = 2l;
  public static long UPDATED_ANIME_ID = 3l;
  public static long DELETED_ANIME_ID = 4l;
  public static long INVALID_ANIME_ID = 30l;

  public static Anime getAnimeToBeSaved() {
    return Anime.builder().name("JoJo's Bizarre Adventure").build();
  }

  public static Anime getFirstAnime() {
    return Anime.builder().id(FIRST_ANIME_ID).name("Naruto").build();
  }

  public static Anime getSecoundAnime() {
    return getFirstAnime().withId(SECOUND_ANIME_ID).withName("One Piece");
  }

  public static Anime getUpdatedAnime() {
    return Anime.builder().id(UPDATED_ANIME_ID).name("The Seven Deadly Sins").build();
  }
}
