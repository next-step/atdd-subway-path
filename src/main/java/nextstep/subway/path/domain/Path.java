package nextstep.subway.path.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class Path {
    private final List<Long> vertexes;
    private final int distance;

    public Path(List<Long> vertexes, int distance) {
        this.vertexes = vertexes;
        this.distance = distance;
    }
}
