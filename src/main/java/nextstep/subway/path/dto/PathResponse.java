package nextstep.subway.path.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PathResponse {

    private List<StationResponse> stations;
    private double distance;

    private PathResponse(List<StationResponse> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(List<StationResponse> stations, double distance) {
        return new PathResponse(stations, distance);
    }
}
