package nextstep.subway.domain;

import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class SubwayMap {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    private SubwayMap(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public static SubwayMap of(List<Station> stations, List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        setVertex(graph, stations);
        setEdge(graph, sections);

        return new SubwayMap(graph);

    }

    private static void setVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Station> stations) {
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private static void setEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

    public SubwayPath findShortestPath(Station source, Station target) {

        if (source.equals(target)) {
            throw new SubwayException(SubwayExceptionMessage.PATH_CANNOT_FIND);
        }

        GraphPath path = getPath(source, target);
        List<Station> shortestPath = path.getVertexList();
        double weight = path.getWeight();
        return new SubwayPath(shortestPath, (int) weight);
    }

    private GraphPath getPath(Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return Optional
                .ofNullable(dijkstraShortestPath.getPath(source, target))
                .orElseThrow(() -> new SubwayException(SubwayExceptionMessage.PATH_CANNOT_FIND));
    }
}
