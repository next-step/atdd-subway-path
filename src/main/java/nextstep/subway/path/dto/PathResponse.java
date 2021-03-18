package nextstep.subway.path.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.domain.Station;

public class PathResponse {

  private List<PathStationResponse> stationResponses;
  private int distance;

  public PathResponse(List<PathStationResponse> pathRespons, int distance){
    this.stationResponses = pathRespons;
    this.distance = distance;
  }

  public static PathResponse of(List<Station> stations, int distance){
    List<PathStationResponse> pathStationResponses = stations.stream()
        .map(PathStationResponse::of)
        .collect(Collectors.toList());
    return new PathResponse(pathStationResponses,distance);
  }

  public List<PathStationResponse> getStationResponses() {
    return stationResponses;
  }

  public int getDistance() {
    return distance;
  }
}
