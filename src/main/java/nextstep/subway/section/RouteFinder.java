package nextstep.subway.section;

import java.util.List;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class RouteFinder {
    private GraphPath path;

    public RouteFinder(List<Section> sectionList, Station source, Station target) {
        this(new Sections(sectionList), source, target);
    }

    public RouteFinder(Sections sections, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        sections.stream().forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
        });
        sections.stream().forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        });

        this.path = new DijkstraShortestPath(graph).getPath(source, target);
    }

    public List<Station> findShortestRoute() {

        return path.getVertexList();
    }

    public int totalDistance() {

        return path.getEdgeList().stream()
            .mapToInt(edge -> (int) path.getGraph().getEdgeWeight(edge))
            .sum();
    }
}