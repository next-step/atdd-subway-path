package nextstep.subway.domain.model;

import java.util.List;

import nextstep.subway.application.DefaultPathFinder;
import nextstep.subway.domain.service.PathFinder;

public class Path {
    private final List<Station> stations;
    private final int distance;

    public Path(List<Station> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static Path of(List<Line> lines, Station source, Station target) {
        PathFinder pathFinder = new DefaultPathFinder(lines);
        Path path = pathFinder.findPath(source, target);
        return new Path(path.getStations(), path.getDistance());
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
