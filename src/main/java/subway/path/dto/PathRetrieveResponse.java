package subway.path.dto;

import lombok.Builder;
import lombok.Getter;
import subway.path.model.Path;
import subway.station.dto.StationResponse;

import java.util.List;

@Getter
@Builder
public class PathRetrieveResponse {
    private List<StationResponse> stations;
    private long distance;

    public static PathRetrieveResponse from(Path path) {
       return PathRetrieveResponse.builder()
               .stations(path.getStations())
               .distance(path.getDistance())
               .build();
    }
}
