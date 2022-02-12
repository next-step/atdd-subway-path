package nextstep.subway.domain;

import java.util.List;

public class ShortestPath<V, D extends Number> {
    public static final ShortestPath<Station, Integer> NONE = new ShortestPath<>(null, null);
    private List<V> paths;
    private D distance;

    private ShortestPath(List<V> paths, D distance) {
        this.paths = paths;
        this.distance = distance;
    }

    public List<V> getPaths() {
        return paths;
    }

    public D getDistance() {
        return distance;
    }

    public boolean isNotExistPath() {
        return this == NONE;
    }

    public static <V, D extends Number> ShortestPath<V, D> of(List<V> paths, D distance) {
        return new ShortestPath<>(paths, distance);
    }
}
