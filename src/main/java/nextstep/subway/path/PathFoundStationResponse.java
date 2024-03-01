package nextstep.subway.path;

import nextstep.subway.station.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathFoundStationResponse {

    private final Long id;
    private final String name;

    public PathFoundStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<PathFoundStationResponse> of(List<Station> stations) {
        return stations.stream()
                .map(PathFoundStationResponse::of)
                .collect(Collectors.toList());
    }

    private static PathFoundStationResponse of(Station station) {
        return new PathFoundStationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
