package nextstep.subway.application.dto;

import lombok.Getter;
import nextstep.subway.domain.Line;

@Getter
public class LineCreateRequest {

  private Long upStationId;
  private Long downStationId;
  private String name;
  private String color;
  private Integer distance;

  public Line to() {
    return new Line(name, color);
  }
}
