package nextstep.study;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class JgraphtTest {
    @Test
    void getDijkstraShortestPath() {
        String source = "v5"; // 시작점
        String target = "v1"; // 도착점

        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        // 각 정점(거쳐갈 수 있는 모든 지점)
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");
        graph.addVertex("v5");
        // 간선(두 정점간 연결된 경로와 거리)
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 20);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 20);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
        graph.setEdgeWeight(graph.addEdge("v1", "v5"), 150);
        graph.setEdgeWeight(graph.addEdge("v3", "v5"), 10);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();
        double pathWeight = dijkstraShortestPath.getPathWeight(source, target);

        assertThat(shortestPath.size()).isEqualTo(4);
        assertThat(shortestPath).containsExactly("v5", "v3", "v2", "v1");
        assertThat(pathWeight).isEqualTo(50);
    }

    // 뭔지 잘 모르곘네?
    @Test
    void getKShortestPaths() {
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
}