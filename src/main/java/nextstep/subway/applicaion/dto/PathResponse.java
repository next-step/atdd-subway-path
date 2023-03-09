package nextstep.subway.applicaion.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import nextstep.subway.domain.Station;

@Getter
public class PathResponse {

    private final List<StationResponse> stations;
    private final int distance;

    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations.stream()
            .map(s -> new StationResponse(s.getId(), s.getName()))
            .collect(Collectors.toList());
        this.distance = distance;
    }

}
