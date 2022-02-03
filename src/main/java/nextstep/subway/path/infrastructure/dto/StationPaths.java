package nextstep.subway.path.infrastructure.dto;

import java.util.List;

import lombok.Getter;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.station.domain.Station;

@Getter
public class StationPaths {
    private final List<Station> stations;
    private final Distance distance;

    public StationPaths(List<Station> stations, Distance distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
