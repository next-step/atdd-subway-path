package nextstep.subway.applicaion.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PathResponse {

    private final List<StationResponse> stations;
    private final int distance;

    @Builder
    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
        this.distance = distance;
    }
}
