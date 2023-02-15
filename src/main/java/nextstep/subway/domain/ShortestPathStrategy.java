package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ShortestPathStrategy<T, E> {
    void init(List<T> vertices, List<E> edges);
    Optional<Path> shortestPath(T source, T target);
    Set<T> allVertices();
}
