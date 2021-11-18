package br.cams7.tests.springwebfluxessentials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class App {

  static {
    BlockHound.install();
  }

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
