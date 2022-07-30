package nextstep.subway.applicaion.dto;

import java.util.List;
import nextstep.subway.domain.Station;

public class PathResponse {

    private final List<Station> stations;
    private final int distance;

    public PathResponse(final List<Station> stations, final int distance) {
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
