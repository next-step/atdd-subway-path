package nextstep.subway.applicaion.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PathResponse {

    private final List<Station> stations;
    private final int distance;

    public static PathResponse from(Path path) {
        return new PathResponse(path.getStations(), path.getDistance());
    }
}
