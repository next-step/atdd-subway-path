package nextstep.subway.path.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GraphTest {

    @Test
    void findPath() {
        List<Long> vertexes = List.of(1L, 2L, 3L);
        List<Edge> edges = List.of(
                new Edge(1L, 2L, 2),
                new Edge(2L, 3L, 3),
                new Edge(1L, 3L, 10));

        Graph pathFinder = new Graph(vertexes, edges);

        Path path = pathFinder.findShortestPath(1L, 3L);

        assertThat(path.getVertexes()).containsExactly(1L, 2L, 3L);
        assertThat(path.getDistance()).isEqualTo(5);
    }
}
