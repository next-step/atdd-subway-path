package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {

    private Integer distance;
    private List<StationResponse> stations;

    public PathResponse() {
    }

    public PathResponse(Integer distance, List<StationResponse> stations) {
        this.distance = distance;
        this.stations = stations;
    }

    public Integer getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
