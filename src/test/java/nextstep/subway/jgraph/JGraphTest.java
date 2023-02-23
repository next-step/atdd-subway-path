package nextstep.subway.jgraph;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JGraphTest {
    WeightedMultigraph<String, DefaultWeightedEdge> graph;

    @BeforeEach
    void setup() {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
    }

    @DisplayName("두 vertices가 주어졌을 때 최단거리를 구한다.")
    @Test
    public void getDijkstraShortestPath() {
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath
            = new DijkstraShortestPath<>(graph);
        List<String> shortestPath
            = dijkstraShortestPath.getPath("v3", "v1").getVertexList();

        assertThat(shortestPath.size()).isEqualTo(3);
    }

    @DisplayName("존재하지 않는 vertex를 넣었을 때 예외를 던진다.")
    @Test
    public void getDijkstraShortestPathWithNoneExistVertex() {
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath
            = new DijkstraShortestPath<>(graph);

        assertThatThrownBy(() -> dijkstraShortestPath.getPath("v3", "v4").getVertexList())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("연결되어 있지 않은 두 vertices를 넣었을 때 null을 반환한다.")
    @Test
    public void getDijkstraShortestPathWithNotConnectedVertices() {
        graph.addVertex("v4");
        graph.addVertex("v5");
        graph.setEdgeWeight(graph.addEdge("v4", "v5"), 2);

        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath
            = new DijkstraShortestPath<>(graph);

        assertThat(dijkstraShortestPath.getPath("v1", "v5")).isNull();
    }
}
