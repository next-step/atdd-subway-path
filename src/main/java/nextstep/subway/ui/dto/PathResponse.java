package nextstep.subway.ui.dto;

import nextstep.subway.domain.Station;

import java.util.List;

public class PathResponse {

    private final List<Station> stations;
    private final Long distance;

    public PathResponse() {
        this.stations = null;
        this.distance = null;
    }

    public PathResponse(final List<Station> stations, final long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
