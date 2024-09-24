package nextstep.subway.path.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.StationResponse;

import java.util.List;

@NoArgsConstructor
@Getter
public class PathResponse {

    List<StationResponse> stations;
    int distance;

    @Builder
    private PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(List<StationResponse> stations, int distance) {
        return PathResponse.builder()
                .stations(stations)
                .distance(distance)
                .build();
    }

}
