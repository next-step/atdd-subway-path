package nextstep.subway.applicaion.dto;

import java.util.List;
import nextstep.subway.domain.Station;

public class PathResponse {
    private List<Station> stations;
    private int distance;

    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
