package nextstep.subway.path;

import nextstep.subway.station.StationResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;
    private Integer distance;

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
