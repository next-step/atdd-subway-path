package nextstep.subway.domain;

import java.util.List;

public interface ShortestPathFindAlgorithm<V, T, D extends Number> {
    ShortestPath<V, D> findShortestPath(V source, V destination, List<T> edges);
}
