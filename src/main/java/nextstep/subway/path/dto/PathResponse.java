package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.Set;

public class PathResponse {

    private final Set<StationResponse> stations;
    private final int distance;

    public PathResponse(Set<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse from(PathDto pathDto) {
        return new PathResponse(pathDto.getStations(), pathDto.getDistance());
    }

    public Set<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
