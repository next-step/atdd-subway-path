package nextstep.subway.domain.entity;

import java.util.List;
import lombok.Getter;

@Getter
public class Path {

    private List<Station> stations;
    private double distance;

    public Path(List<Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
