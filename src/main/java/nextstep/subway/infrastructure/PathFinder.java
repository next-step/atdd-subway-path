package nextstep.subway.infrastructure;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.ShortestRoute;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class PathFinder {

    private DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = initGraph(lines);
        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> initGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initVertex(graph, lines);
        initEdge(graph, lines);
        return graph;
    }

    private void initVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        List<Station> stations = lines.stream()
                .flatMap(l -> l.stations().stream())
                .distinct()
                .collect(toList());

        for(Station station : stations) {
            graph.addVertex(station);
        }
    }

    private void initEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        List<Section> sections = lines.stream()
                .flatMap(l -> l.sections().stream())
                .collect(toList());

        for(Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance().getValue());
        }
    }

    public ShortestRoute findRoutes(Station source, Station target) {
        throwsExceptionSourceAndTargetEquals(source, target);

        try {
            List<Station> vertexes = dijkstraShortestPath.getPath(source, target).getVertexList();
            double distance = dijkstraShortestPath.getPath(source, target).getWeight();
            return new ShortestRoute(vertexes, distance);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }
    }

    private void throwsExceptionSourceAndTargetEquals(Station source, Station target) {
        if(source.equals(target)) {
            throw new IllegalArgumentException();
        }
    }
}
