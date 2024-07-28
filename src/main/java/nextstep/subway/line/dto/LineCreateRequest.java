package nextstep.subway.line.dto;

import nextstep.subway.line.Line;
import nextstep.subway.Station;
import nextstep.subway.line.LineSections;

public class LineCreateRequest {

  private String name;
  private String color;
  private Long upStationId;
  private Long downStationId;
  private int distance;

  public LineCreateRequest() {
  }

  public LineCreateRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
    this.name = name;
    this.color = color;
    this.upStationId = upStationId;
    this.downStationId = downStationId;
    this.distance = distance;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public Long getUpStationId() {
    return upStationId;
  }

  public Long getDownStationId() {
    return downStationId;
  }

  public int getDistance() {
    return distance;
  }
}
