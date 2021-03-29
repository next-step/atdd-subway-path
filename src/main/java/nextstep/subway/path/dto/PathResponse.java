package nextstep.subway.path.dto;

import nextstep.subway.path.domain.Path;
import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class PathResponse {
    private List<Station> stations;
    private double distance;

    public PathResponse() {
        this.stations = new ArrayList<>();
    }

    private PathResponse(List<Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(Path path) {
        return new PathResponse(path.getStations(), path.getDistance());
    }

    public List<Station> getStations() {
        return this.stations;
    }

    public double getDistance() {
        return this.distance;
    }
}
