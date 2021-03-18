package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.List;

public class PathResponse {
    private PathResponse(){
    }

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    private List<StationResponse> stations;
    private int distance;

    public static PathResponse of(List<StationResponse> stations, double weight) {
        return new PathResponse(stations, (int) weight);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
