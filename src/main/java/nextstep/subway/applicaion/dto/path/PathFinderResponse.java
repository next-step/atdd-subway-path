package nextstep.subway.applicaion.dto.path;

import nextstep.subway.applicaion.dto.station.StationResponse;

import java.util.List;

public class PathFinderResponse {

    private List<StationResponse> stations;
    private int distance;

    public PathFinderResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
