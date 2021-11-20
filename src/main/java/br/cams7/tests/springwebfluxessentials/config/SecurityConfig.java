package br.cams7.tests.springwebfluxessentials.config;

import br.cams7.tests.springwebfluxessentials.service.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    // @formatter:off
    return http.csrf()
        .disable()
        .authorizeExchange()
        .pathMatchers(HttpMethod.POST, "/animes/**")
        .hasRole("ADMIN")
        .pathMatchers(HttpMethod.GET, "/animes/**")
        .hasRole("USER")
        .anyExchange()
        .authenticated()
        .and()
        .formLogin()
        .and()
        .httpBasic()
        .and()
        .build();
    // @formatter:on
  }

  @Bean
  public ReactiveAuthenticationManager authenticationManager(UserDetailsService service) {
    return new UserDetailsRepositoryReactiveAuthenticationManager(service);
  }

  // public static void main(String[] args) {
  //    System.out.println("Password:
  // "+PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("abc12345"));
  // }
}
