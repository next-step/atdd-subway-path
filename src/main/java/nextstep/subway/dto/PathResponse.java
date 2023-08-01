package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.domain.Path;

@Getter
@AllArgsConstructor
public class PathResponse {
  private List<StationResponse> stations;
  private Long distance;

  public static PathResponse from(Path path) {
    return new PathResponse(path.getStations().stream().map(StationResponse::from).collect(
        Collectors.toList()), path.getDistance());
  }
}
