package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PathResponse {
    private List<PathStationResponse> stations = new ArrayList<>();
    private Long distance;

    public PathResponse(List<PathStationResponse> stations, Long distance) {
        this.stations.addAll(stations);
        this.distance = distance;
    }
}
