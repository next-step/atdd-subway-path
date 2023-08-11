package nextstep.subway.applicaion.dto.path;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PathSearchRequest {

  @Positive(message = "시작역 번호는 음수일 수 없습니다.")
  @NotNull(message = "시작역 번호는 필수입니다.")
  private Long source;

  @Positive(message = "도착역 번호는 음수일 수 없습니다.")
  @NotNull(message = "도착역 번호는 필수입니다.")
  private Long target;
}
