package subway.path.dto;

import lombok.Builder;
import lombok.Getter;
import subway.station.dto.StationResponse;

import java.util.List;

@Getter
@Builder
public class PathRetrieveResponse {
    private List<StationResponse> stations;
    private long distance;
}
