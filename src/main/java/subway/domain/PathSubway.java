package subway.domain;

import lombok.Getter;

import java.util.List;
/**
 * 경로 탐색 결과
 */
public class PathSubway {
    /**
     * 경로의 거리 총합
     */
    @Getter
    private final Kilometer distance;
    /**
     * 경로에 포함된 역 목록
     */
    private final PathStations stations;

    private PathSubway(Kilometer distance, List<PathStation> stations) {
        this.distance = distance;
        this.stations = PathStations.from(stations);
    }

    public static PathSubway of(Kilometer distance, List<PathStation> stations) {
        PathSubway pathSubway = new PathSubway(distance, stations);
        pathSubway.validate();
        return pathSubway;
    }

    private void validate() {
        stations.validate();
        distance.validate();
    }
    public List<PathStation> getStations() {
        return stations.getStations();
    }

}
