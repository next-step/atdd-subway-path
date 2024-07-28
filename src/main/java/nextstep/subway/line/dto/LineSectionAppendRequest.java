package nextstep.subway.line.dto;

public class LineSectionAppendRequest {
  private Long upStationId;
  private Long downStationId;
  private int distance;

  public LineSectionAppendRequest() {
  }

  public LineSectionAppendRequest(Long upStationId, Long downStationId, int distance) {
    this.downStationId = downStationId;
    this.upStationId = upStationId;
    this.distance = distance;

    if(upStationId == null || downStationId == null) {
      throw new IllegalArgumentException("올바르지 않은 입력 값입니다.");
    }
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
