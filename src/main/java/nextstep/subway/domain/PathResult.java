package nextstep.subway.domain;

import java.util.List;

public class PathResult {

    private List<Station> stations;
    private Integer distance;

    public PathResult(final List<Station> stations, final Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResult of(final List<Station> stations, final double pathWeight) {
        return new PathResult(stations, Double.valueOf(pathWeight).intValue());
    }

    public List<Station> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
