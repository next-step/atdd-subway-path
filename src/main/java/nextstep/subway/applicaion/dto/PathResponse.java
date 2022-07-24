package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.StationPath;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PathResponse {

    private final List<StationResponse> stations;
    private final int distance;

    public static PathResponse of(StationPath stationPath) {
        return new PathResponse(
                stationPath.getStations().stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toUnmodifiableList()),
                stationPath.getDistance());
    }
}
