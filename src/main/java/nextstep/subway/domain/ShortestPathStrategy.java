package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ShortestPathStrategy<V, E> {
    void init(List<E> edges);
    Optional<Path> shortestPath(V source, V target);
    Set<V> allVertices();
}
