package nextstep.subway.applicaion.dto;


import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;
    private long distance;

    public PathResponse(List<StationResponse> stations, double distance) {
        this.stations = stations;
        this.distance = (long) distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public long getDistance() {
        return distance;
    }
}
