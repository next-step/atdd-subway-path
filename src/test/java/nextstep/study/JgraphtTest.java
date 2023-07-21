package nextstep.study;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


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
        System.out.println(paths);

        assertThat(paths).hasSize(2);
        paths.stream()
                .forEach(it -> {
                    assertThat(it.getVertexList()).startsWith(source);
                    assertThat(it.getVertexList()).endsWith(target);
                });
    }

    @DisplayName("다익스트라 알고리즘 Section에 적용해보기")
    @Test
    void dijkstraToSection() {
        Station sourceStation = new Station(1L, "출발역");
        Station targetStation = new Station(2L, "도착역");
        Station station1 = new Station(3L, "다른역1");
        Station station2 = new Station(4L, "다른역2");

        Section section1 = new Section(sourceStation, station1, 10);
        Section section2 = new Section(station1, targetStation, 15);

        Section section3 = new Section(sourceStation, station2, 1);
        Section section4 = new Section(station2, targetStation, 1);

        WeightedMultigraph<Station, DefaultWeightedEdge> sectionGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sectionGraph.addVertex(sourceStation);
        sectionGraph.addVertex(targetStation);
        sectionGraph.addVertex(station1);
        sectionGraph.addVertex(station2);

        sectionGraph.setEdgeWeight(sectionGraph.addEdge(section1.getUpStation(), section1.getDownStation()), section1.getDistance());
        sectionGraph.setEdgeWeight(sectionGraph.addEdge(section2.getUpStation(), section2.getDownStation()), section2.getDistance());
        sectionGraph.setEdgeWeight(sectionGraph.addEdge(section3.getUpStation(), section3.getDownStation()), section3.getDistance());
        sectionGraph.setEdgeWeight(sectionGraph.addEdge(section4.getUpStation(), section4.getDownStation()), section4.getDistance());

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(sectionGraph);
        List<Station> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation)
                .getVertexList();

        System.out.println(shortestPath);
        assertThat(shortestPath.size()).isEqualTo(3);
    }
}