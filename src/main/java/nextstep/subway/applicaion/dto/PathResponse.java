package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;

public class PathResponse {

    private long distance;
    private List<Station> stations;

    public long getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public PathResponse(long distance, List<Station> stations) {
        this.distance = distance;
        this.stations = stations;
    }
}
