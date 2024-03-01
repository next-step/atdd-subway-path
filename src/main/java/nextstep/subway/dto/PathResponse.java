package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Path;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PathResponse {
    List<StationResponse> stations;
    int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(List<StationResponse> stationResponses, int distance) {
        return new PathResponse(stationResponses, distance);
    }

    public static PathResponse of(Path path) {
        List<StationResponse> stationResponses = path.getStations().stream().map(StationResponse::createStationResponse).collect(Collectors.toList());
        return new PathResponse(stationResponses, path.getDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
