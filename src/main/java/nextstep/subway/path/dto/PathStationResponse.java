package nextstep.subway.path.dto;

import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;

public class PathStationResponse {
  private Long id;
  private String name;
  private LocalDateTime createdAt;

  public PathStationResponse() {
  }

  public static PathStationResponse of(Station station){
    return new PathStationResponse(station.getId(),station.getName(),station.getCreatedDate());
  }

  public PathStationResponse(Long id, String name, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public LocalDateTime getCreatedDate() {
    return createdAt;
  }


}
