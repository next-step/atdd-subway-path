package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.object.Distance;
import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;

    private Integer distance;

    public PathResponse(List<StationResponse> stations, Distance distance) {
        this.stations = stations;
        this.distance = distance.getValue();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
