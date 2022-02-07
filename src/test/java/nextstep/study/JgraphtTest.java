package nextstep.study;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


class JgraphtTest {

    @DisplayName("최단 경로를 반환한다")
    @Test
    void getDijkstraShortestPath() {
        // 출발 도착
        String source = "v3";
        String target = "v1";

        // 가중치 그래프
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        // 노드 입력
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");

        // 간선 입력
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 3);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
        graph.setEdgeWeight(graph.addEdge("v2", "v4"), 1);
        graph.setEdgeWeight(graph.addEdge("v3", "v4"), 1);

        // 가장 짤은 거리 클래스 생성후, 결과값(경로) 가져오기
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();

        assertAll(
                () -> assertThat(shortestPath.size()).isEqualTo(4),
                () -> assertThat(shortestPath).containsExactly("v3", "v4", "v2", "v1")
        );
    }

    @DisplayName("최단 경로의 총 거리값을 반환한다")
    @Test
    void getDijkstraShortestWeight() {
        // 출발 도착
        String source = "v3";
        String target = "v1";

        // 가중치 그래프
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        // 노드 입력
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");

        // 간선 입력
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 3);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
        graph.setEdgeWeight(graph.addEdge("v2", "v4"), 1);
        graph.setEdgeWeight(graph.addEdge("v3", "v4"), 1);

        // 가장 짤은 거리 클래스 생성후, 결과값(거리) 가져오기
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        final double pathWeight = dijkstraShortestPath.getPathWeight(source, target);
        assertThat(pathWeight).isEqualTo(4.0);
    }

    @DisplayName("경로들을 최단 거리를 최우선 순으로 반환한다")
    @Test
    void getKShortestPaths() {
        // 출발 도착
        String source = "v3";
        String target = "v1";

        // 가중치 그래프
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        // 노드 입력
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");

        // 간선 입력
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 3);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
        graph.setEdgeWeight(graph.addEdge("v2", "v4"), 1);
        graph.setEdgeWeight(graph.addEdge("v3", "v4"), 1);

        // DijkstraShortestPath 가 아닌, KShortestPaths 사용한다.
        // 또한 getPath()가 아니라, getPaths()이다.
        // k는 경로의 수, 즉 limit 같은 역할이다.
        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(source, target);

        // 모든 경로를 뽑지만, 우선순위로 나오는 것 같다.
        assertThat(paths).hasSize(3);
        paths.stream()
                .forEach(it -> {
                    assertThat(it.getVertexList()).startsWith(source);
                    assertThat(it.getVertexList()).endsWith(target);
                });
    }
}
