package nextstep.subway.applicaion.dto;

import static java.util.stream.Collectors.*;

import java.util.List;

import nextstep.subway.domain.SubwayPath;

public class PathResponse {
    private final List<StationResponse> stations;
    private final int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse createResponse(SubwayPath subwayPath) {
        List<StationResponse> stations = subwayPath.getStations().stream()
            .map(StationResponse::createResponse)
            .collect(toList());

        return new PathResponse(stations, subwayPath.getTotalDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
