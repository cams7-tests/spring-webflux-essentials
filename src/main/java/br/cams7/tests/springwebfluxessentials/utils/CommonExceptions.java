package br.cams7.tests.springwebfluxessentials.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public final class CommonExceptions {
  public static <T> Mono<T> responseNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
}
