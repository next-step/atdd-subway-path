package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.Set;

public class PathDto {

    private final Set<StationResponse> stations;
    private final int distance;

    public PathDto(Set<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public Set<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
