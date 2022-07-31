package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;

import java.util.List;

public class PathResponse {
    private List<Station> stations;
    private int distance;

    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public PathResponse(PathFinder path, Station source, Station target) {
        this.stations = path.getShortestPath(source, target);
        this.distance = path.getShortestDistance(source, target);
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
