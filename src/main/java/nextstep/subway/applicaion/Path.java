package nextstep.subway.applicaion;

import java.util.List;
import lombok.Getter;
import nextstep.subway.domain.Station;

@Getter
public class Path {
    private List<Station> stations;
    private Integer distance;

    public Path(List<Station> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
