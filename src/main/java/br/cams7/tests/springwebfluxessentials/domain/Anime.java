package br.cams7.tests.springwebfluxessentials.domain;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("anime.anime")
public class Anime {
  @Id private Long id;

  @NotBlank(message = "The name of this anime cannot be empty")
  private String name;
}
