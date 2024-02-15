package nextstep.subway.line.util;

import nextstep.subway.station.Station;

import java.util.List;

public class Path {
    private List<Station> path;
    private double distance;

    public Path(List<Station> path, double distance) {
        this.path = path;
        this.distance = distance;
    }

    public List<Station> getPath() {
        return path;
    }

    public double getDistance() {
        return distance;
    }
}
