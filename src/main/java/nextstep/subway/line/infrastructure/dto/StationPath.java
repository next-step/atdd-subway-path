package nextstep.subway.line.infrastructure.dto;

import java.util.List;

import lombok.Getter;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.station.domain.Station;

@Getter
public class StationPath {
    private final List<Station> stations;
    private final Distance distance;

    public StationPath(List<Station> stations, Distance distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
