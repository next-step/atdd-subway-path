package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Paths;

import java.util.List;
import java.util.stream.Collectors;

public class PathsResponse {
    private List<StationResponse> stations;
    private int distance;

    private PathsResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathsResponse of(Paths paths){
        List<StationResponse> stationResponses = paths.getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toUnmodifiableList());
        return new PathsResponse(stationResponses, paths.getDistance());

    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
