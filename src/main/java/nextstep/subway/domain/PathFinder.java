package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    public PathFinder(List<Line> lines) {
        lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(this::initializeGraph);
    }

    private void initializeGraph(Section section) {
        graph.addVertex(section.getUpStation());
        graph.addVertex(section.getDownStation());
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public Path findShortestPath(Station sourceStation, Station targetStation) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        return new Path(graphPath.getVertexList(), graphPath.getWeight());
    }
}
