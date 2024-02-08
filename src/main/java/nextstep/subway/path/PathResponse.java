package nextstep.subway.path;

import lombok.AllArgsConstructor;
import nextstep.subway.station.StationResponse;

import java.util.List;

@AllArgsConstructor
public class PathResponse {
    private List<StationResponse> stations;
    private int distance;
}
