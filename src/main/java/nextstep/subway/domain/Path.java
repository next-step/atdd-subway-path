package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;

public class Path {
    private final List<Station> stations;
    private final double distance;

    public Path(List<Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Double.compare(path.distance, distance) == 0 && Objects.equals(stations, path.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations, distance);
    }
}
