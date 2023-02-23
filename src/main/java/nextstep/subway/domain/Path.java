package nextstep.subway.domain;

import lombok.RequiredArgsConstructor;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Path {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    private final DijkstraShortestPath dijkstraShortestPath;

    private Path() {
        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public Path(final List<Line> lines) {
        init(lines);
        this.dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public List<Station> findShortestPath(final Station sourceStation, final Station targetStation) {
        return dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
    }

    public int findShortestPathDistance(final Station sourceStation, final Station targetStation) {
        return (int) dijkstraShortestPath.getPath(sourceStation, targetStation).getWeight();
    }

    private void init(final List<Line> lines) {
        for (final Line line : lines) {
            final List<Station> stations = line.getStations();
            stations.forEach(graph::addVertex);
            final List<Section> sections = line.getSections();
            sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        }
    }
}
