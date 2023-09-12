package subway.domain;

import java.util.List;

public class PathStations {
    private static final int MINIMUM_STATION_COUNT = 2;
    private static final int START_STATION_INDEX = 0;
    private final List<PathStation> stations;

    private PathStations(List<PathStation> stations) {
        this.stations = stations;
    }

    public static PathStations from(List<PathStation> stations) {
        return new PathStations(stations);
    }

    private boolean isCircular() {
        return stations.get(START_STATION_INDEX).equals(stations.get(stations.size() - 1));
    }

    public void validate() {
        if (stations.isEmpty()) {
            throw new IllegalArgumentException("경로에 포함된 역이 없습니다.");
        }
        if (stations.size() < MINIMUM_STATION_COUNT) {
            throw new IllegalArgumentException("경로를 찾을 수 없습니다.");
        }
        if (isCircular()) {
            throw new IllegalArgumentException("경로가 순환되었습니다.");
        }

        stations.forEach(PathStation::validate);

    }

    public List<PathStation> getStations() {
        return stations;
    }
}
