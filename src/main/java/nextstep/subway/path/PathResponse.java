package nextstep.subway.path;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.station.StationResponse;

import java.util.List;

@Getter
@AllArgsConstructor
public class PathResponse {
    private List<StationResponse> stations;
    private int distance;
}
