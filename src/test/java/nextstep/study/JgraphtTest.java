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
        String 역1 = "v1";
        String 역2 = "v2";
        String 역3 = "v3";

        // 역3 -> 역1로 가는 최단 경로
        String 출발 = 역3;
        String 도착 = 역1;

        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex(역1);
        graph.addVertex(역2);
        graph.addVertex(역3);
        graph.setEdgeWeight(graph.addEdge(역1, 역2), 2); // 구간1 - 거리2
        graph.setEdgeWeight(graph.addEdge(역2, 역3), 2); // 구간2 - 거리2
        graph.setEdgeWeight(graph.addEdge(역1, 역3), 100); // 구간3 - 거리3

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath graphPath = dijkstraShortestPath.getPath(출발, 도착);
        List<String> shortestPath = graphPath.getVertexList();

        assertThat(shortestPath.size()).isEqualTo(3);
        // 역3 -> 역2 거리2
        // 역3 -> 역1 거리100 
        // 역3 -> 역2가 더짧으므로 역3 -> 역2 -> 역1 순으로 탐색함
        assertThat(shortestPath).containsExactly(역3, 역2, 역1);
        assertThat(graphPath.getWeight()).isEqualTo(4.0);
    }

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