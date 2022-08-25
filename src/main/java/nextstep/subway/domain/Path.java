package nextstep.subway.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class Path {

    private List<Station> stations;
    private Integer distance;

    public Path(List<Station> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

}
