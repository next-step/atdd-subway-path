package nextstep.subway.applicaion.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public static PathResponse of(List<Station> stations, int distance) {
        List<StationResponse> stationResponses = stations.stream()
                                                         .map(StationResponse::from)
                                                         .collect(Collectors.toList());
        return new PathResponse(stationResponses, distance);
    }
}
