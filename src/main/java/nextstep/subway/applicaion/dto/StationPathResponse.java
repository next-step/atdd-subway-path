package nextstep.subway.applicaion.dto;

import java.time.LocalDateTime;

public class StationPathResponse {
  private final Long id;
  private final String name;
  private final LocalDateTime createdDate;

  public StationPathResponse(Long id, String name, LocalDateTime createdDate) {
    this.id = id;
    this.name = name;
    this.createdDate = createdDate;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }
}
