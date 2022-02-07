package nextstep.subway.applicaion.dto;

import java.time.LocalDateTime;
import java.util.List;

public class LineResponse {
  private final Long id;
  private final String name;
  private final String color;
  private final List<StationResponse> stations;
  private final LocalDateTime createdDate;
  private final LocalDateTime modifiedDate;

  public LineResponse(Long id, String name, String color, List<StationResponse> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
    this.id = id;
    this.name = name;
    this.color = color;
    this.stations = stations;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public List<StationResponse> getStations() {
    return stations;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public LocalDateTime getModifiedDate() {
    return modifiedDate;
  }
}

