package nextstep.subway.applicaion.path.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.station.response.StationResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PathResponse {
    private List<StationResponse> stations;
    private long distance;

    public PathResponse(final List<StationResponse> stations, final long distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
