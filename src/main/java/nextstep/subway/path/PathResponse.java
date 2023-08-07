package nextstep.subway.path;

import nextstep.subway.station.StationResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;
    private Integer distance;

    private PathResponse() {
    }

    public PathResponse(List<StationResponse> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
