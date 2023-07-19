package subway.path.model;

import lombok.Builder;
import lombok.Getter;
import subway.station.dto.StationResponse;

import java.util.List;

@Getter
@Builder
public class Path {
    private List<StationResponse> stations;
    private long distance;
}
