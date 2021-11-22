package br.cams7.tests.springwebfluxessentials.domain;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("tb_anime")
public class Anime {
  @Id
  @Column("id_anime")
  private Long id;

  @NotBlank(message = "The name of this anime cannot be empty")
  private String name;
}
