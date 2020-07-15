package nextstep.study.jgraph;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JgraphTest {
    @Test
    public void getDijkstraShortestPath() {
        String source = "v3";
        String target = "v1";
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();

        assertThat(shortestPath.size()).isEqualTo(3);
    }

    @DisplayName("경로들을 여러개 가져온다")
    @Test
    public void getKShortestPaths() {
        String source = "v3";
        String target = "v1";

        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(source, target);

        assertThat(paths).hasSize(2);
        paths.stream()
                .forEach(it -> {
                    assertThat(it.getVertexList()).startsWith(source);
                    assertThat(it.getVertexList()).endsWith(target);
                });
    }

    @DisplayName("가장 가까운 경로를 1개만 가져온다")
    @Test
    public void getKShortestPath() {
        String source = "v1";
        String target = "v4";

        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        /**
         *   / 2 -\
         * 1 - 3 - 4
         */
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 1);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 1);
        graph.setEdgeWeight(graph.addEdge("v2", "v4"), 2);
        graph.setEdgeWeight(graph.addEdge("v3", "v4"), 1);

        List<GraphPath> paths = new KShortestPaths(graph, 1).getPaths(source, target);

        assertThat(paths).hasSize(1);
        assertThat(paths.get(0).getWeight()).isEqualTo(2);
        assertThat(paths.get(0).getStartVertex()).isEqualTo(source);
        assertThat(paths.get(0).getEndVertex()).isEqualTo(target);
    }

    @DisplayName("경로가 이어지지 않는 경우")
    @Test
    public void ifPathsAreNotConnected() {
        String source = "v1";
        String target = "v3";

        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        /**
         * 1 - 2  3
         */
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 1);

        List<GraphPath> paths = new KShortestPaths(graph, 1).getPaths(source, target);

        assertThat(paths).isEmpty();
    }
}
