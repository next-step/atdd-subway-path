package nextstep.study;

import nextstep.subway.domain.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


class JgraphtTest {
    @Test
    void getDijkstraShortestPath() {
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

    /**
     * 지하철 기반 최단경로 탐색
     */
    @Test
    void 지하철역_최단경로탐색() {
        // given
        /**
         * 출발역 --- 10 --- 강남역 --- 15 --- 도착역
         * 출발역 --- 5 --- 역삼역 --- 10 --- 도착역
         */
        Station 출발역 = new Station("출발역");
        Station 도착역 = new Station("도착역");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex(출발역);
        graph.addVertex(도착역);
        graph.addVertex(강남역);
        graph.addVertex(역삼역);
        graph.setEdgeWeight(graph.addEdge(출발역, 강남역), 10);
        graph.setEdgeWeight(graph.addEdge(강남역, 도착역), 15);
        graph.setEdgeWeight(graph.addEdge(출발역, 역삼역), 5);
        graph.setEdgeWeight(graph.addEdge(역삼역, 도착역), 10);

        // when
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> shortestPath = dijkstraShortestPath.getPath(출발역, 도착역).getVertexList();

        // then
        assertAll(
                () -> assertThat(shortestPath).startsWith(출발역),
                () -> assertThat(shortestPath).endsWith(도착역),
                () -> assertThat(shortestPath).hasSize(3),
                () -> assertThat(shortestPath).contains(역삼역)
        );
    }
}