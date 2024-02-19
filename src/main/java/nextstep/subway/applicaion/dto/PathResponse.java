package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stations;
    private Integer distance;

    public PathResponse(List<StationResponse> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public PathResponse() {

    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }

}
