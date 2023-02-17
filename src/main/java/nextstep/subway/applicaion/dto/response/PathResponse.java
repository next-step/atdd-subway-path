package nextstep.subway.applicaion.dto.response;

import nextstep.subway.domain.Path;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    private PathResponse() {
    }

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(Path path) {
        return new PathResponse(path.getStations()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toUnmodifiableList()),
                (int) path.getDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
