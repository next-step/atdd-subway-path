package nextstep.subway.applicaion.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindPathRequest {

  @NotNull
  // source station ID
  private Long source;

  @NotNull
  // target station ID
  private Long target;
}
