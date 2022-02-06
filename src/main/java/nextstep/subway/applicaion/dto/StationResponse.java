package nextstep.subway.applicaion.dto;

import java.time.LocalDateTime;

public class StationResponse {
  private final Long id;
  private final String name;
  private final LocalDateTime createdDate;
  private final LocalDateTime modifiedDate;

  public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
    this.id = id;
    this.name = name;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
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

  public LocalDateTime getModifiedDate() {
    return modifiedDate;
  }
}
