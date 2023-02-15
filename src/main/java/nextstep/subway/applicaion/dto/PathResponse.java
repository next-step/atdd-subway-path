package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.PathResult;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private List<StationResponse> stations;
    private Integer distance;

    private PathResponse() {}

    private PathResponse(final List<StationResponse> stationResponses, final Integer pathWeight) {
        this.stations = stationResponses;
        this.distance = pathWeight;
    }

    public static PathResponse from(final PathResult pathResult) {
        final List<StationResponse> stationResponses = pathResult.getStations()
                .stream()
                .map(StationResponse::createStationResponse)
                .collect(Collectors.toList());
        return new PathResponse(stationResponses, pathResult.getDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
