package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.domain.Path;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

    public static PathResponse from(Path path) {
        return new PathResponse(
                path.getStations()
                        .stream()
                        .map(StationResponse::from)
                        .collect(Collectors.toList()),
                path.getDistance()
        );
    }

}
