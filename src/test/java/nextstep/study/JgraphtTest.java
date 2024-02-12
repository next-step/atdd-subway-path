package nextstep.study;

import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.StationFixture.*;
import static nextstep.subway.fixture.StationFixture.YEOKSAM_STATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class JgraphtTest {

    /**
     * 강남역 --10-- 선릉역 --20-- 양재역 --30-- 역삼역
     */
    @Test
    void 지하철역_기반_최단경로_테스트() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        Station 강남역 = GANGNAM_STATION.toStation(1L);
        Station 선릉역 = SEOLLEUNG_STATION.toStation(2L);
        Station 양재역 = YANGJAE_STATION.toStation(3L);
        Station 역삼역 = YEOKSAM_STATION.toStation(4L);
        graph.addVertex(강남역);
        graph.addVertex(선릉역);
        graph.addVertex(양재역);
        graph.addVertex(역삼역);
        graph.setEdgeWeight(graph.addEdge(강남역, 선릉역), 10);
        graph.setEdgeWeight(graph.addEdge(선릉역, 양재역), 20);
        graph.setEdgeWeight(graph.addEdge(양재역, 역삼역), 30);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(강남역, 역삼역);
        List<Station> vertexes = shortestPath.getVertexList();

        assertAll(
                () -> assertThat(shortestPath.getWeight()).isEqualTo(60),
                () -> assertThat(vertexes.size()).isEqualTo(4)
        );
    }

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
}
