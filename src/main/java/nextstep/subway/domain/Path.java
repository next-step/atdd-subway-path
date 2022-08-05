package nextstep.subway.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class Path {

    List<Station> paths;

    int distance;

    public Path() {

    }

    public Path(List<Station> paths, int distance) {
        this.paths = paths;
        this.distance = distance;
    }
}