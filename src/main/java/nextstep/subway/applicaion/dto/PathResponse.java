package nextstep.subway.applicaion.dto;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PathResponse {

    private List<StationResponse> stations;
    private Integer distance;

    public PathResponse() {}

    public PathResponse(List<StationResponse> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
