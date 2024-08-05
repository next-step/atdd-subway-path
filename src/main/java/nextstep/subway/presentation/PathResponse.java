package nextstep.subway.presentation;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public static PathResponse of(List<Station> stations, int distance) {
        List<StationResponse> stationResponses = stations.stream().map(StationResponse::of).collect(Collectors.toList());
        return PathResponse.builder()
                .stations(stationResponses)
                .distance(distance)
                .build();
    }
}
