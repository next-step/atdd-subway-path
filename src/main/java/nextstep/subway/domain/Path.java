package nextstep.subway.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class Path {
    private List<Station> stations;
    private int distance;

    public Path(List<Station> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
