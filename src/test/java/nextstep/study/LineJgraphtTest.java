package nextstep.study;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.station;
import static org.assertj.core.api.Assertions.assertThat;

public class LineJgraphtTest {

    @Test
    void 최단거리조회_1구간() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);

        line.addSection(section(station1, station2));
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(station1);
        graph.addVertex(station2);

        graph.setEdgeWeight(graph.addEdge(station1, station2), 10);


        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        List<Station> result = dijkstraShortestPath.getPath(station1, station2).getVertexList();
        assertThat(result).hasSize(2);
    }

    @Test
    void 최단거리조회_2구간() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        line.addSection(section(station2, station3));
        line.addSection(section(station1, station2));

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(station1);
        graph.addVertex(station2);
        graph.addVertex(station3);

        graph.setEdgeWeight(graph.addEdge(station2, station3), 10);
        graph.setEdgeWeight(graph.addEdge(station1, station2), 10);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        List<Station> result = dijkstraShortestPath.getPath(station1, station3).getVertexList();
        assertThat(result).hasSize(3);
    }

    @Test
    void 최단거리조회_3구간() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        // when (4, 3), (3, 1), (1, 2) -> (1, 2), (3, 1), (4, 3)
        line.addSection(section(station1, station2));
        line.addSection(section(station3, station1));
        line.addSection(section(station4, station3));

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graph.addVertex(station1);
        graph.addVertex(station2);
        graph.addVertex(station3);
        graph.addVertex(station4);

        graph.setEdgeWeight(graph.addEdge(station1, station2), 10);
        graph.setEdgeWeight(graph.addEdge(station3, station1), 10);
        graph.setEdgeWeight(graph.addEdge(station4, station3), 10);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        List<Station> result = dijkstraShortestPath.getPath(station2, station4).getVertexList();
        assertThat(result).hasSize(4);
    }

}
