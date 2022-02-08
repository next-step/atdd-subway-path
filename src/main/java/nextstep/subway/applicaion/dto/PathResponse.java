package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.object.Distance;
import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;

    private Distance distance;

    public PathResponse(List<StationResponse> stations, Distance distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Distance getDistance() {
        return distance;
    }
}
