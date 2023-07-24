package nextstep.subway.section;

public class SectionRequest {
  private Long upStationId;
  private Long downStationId;
  private Long distance;

  public SectionRequest() {
  }

  public SectionRequest(final Long upStationId, final Long downStationId, final Long distance) {
      this.upStationId = upStationId;
      this.downStationId = downStationId;
      this.distance = distance;
  }

  public Long getDownStationId() {
    return downStationId;
  }

  public Long getUpStationId() {
    return upStationId;
  }

  public Long getDistance() {
    return distance;
  }
}
