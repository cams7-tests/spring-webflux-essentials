package br.cams7.tests.springwebfluxessentials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

  // static {
  //  BlockHound.install(
  //      buider ->
  //          buider
  // .allowBlockingCallsInside("java.util.UUID", "randomUUID")
  //              .allowBlockingCallsInside("java.io.InputStream", "readNBytes")
  //              .allowBlockingCallsInside("java.io.FilterInputStream", "read"));
  // }

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
