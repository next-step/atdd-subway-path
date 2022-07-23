package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stations;
    private Double distance;

    public PathResponse() {
    }

    public PathResponse(List<StationResponse> stations, Double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Double getDistance() {
        return distance;
    }
}
