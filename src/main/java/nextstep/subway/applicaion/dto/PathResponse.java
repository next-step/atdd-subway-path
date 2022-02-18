package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
  private final List<StationPathResponse> stations;
  private final int distance;

  public PathResponse(List<StationPathResponse> stations, int distance) {
    this.stations = Collections.unmodifiableList(stations);
    this.distance = distance;
  }

  public static PathResponse of(List<Station> stations, int distance) {
    List<StationPathResponse> pathResponse = stations.stream()
      .map(station -> new StationPathResponse(station.getId(), station.getName(), station.getCreatedDate()))
      .collect(Collectors.toList());
    return new PathResponse(pathResponse, distance);
  }

  public List<StationPathResponse> getStations() {
    return stations;
  }

  public int getDistance() {
    return distance;
  }
}
