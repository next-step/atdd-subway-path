package nextstep.subway.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Path {
    private List<Station> path;
    private double distance;

    @Builder
    public Path(List<Station> path, double distance) {
        this.path = path;
        this.distance = distance;
    }

}
