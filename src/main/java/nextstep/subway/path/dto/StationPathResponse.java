package nextstep.subway.path.dto;

import java.util.List;

public class StationPathResponse {

  private List<PathStationResponse> stationResponses;
  private int distance;

  public StationPathResponse(List<PathStationResponse> pathStationResponses, int distance){
    this.stationResponses = pathStationResponses;
    this.distance = distance;
  }

  public static StationPathResponse of(List<PathStationResponse> pathStationResponses, int distance){
    return new StationPathResponse(pathStationResponses,distance);
  }

  public List<PathStationResponse> getStationResponses() {
    return stationResponses;
  }

  public int getDistance() {
    return distance;
  }
}
