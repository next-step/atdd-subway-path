package nextstep.subway.domain;

import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.FindPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class SubwayMap {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    private SubwayMap(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public static SubwayMap create(List<Line> lines) {
        WeightedMultigraph graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.forEach(line -> {
            line.getStations().forEach(graph::addVertex);
            line.getSections().forEach(section -> graph.setEdgeWeight(graph.addEdge(
                    section.getUpStation(), section.getDownStation()), section.getDistance()));
        });
        return new SubwayMap(graph);
    }

    public Path findShortestPath(Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath shortestPath = dijkstraShortestPath.getPath(source, target);
        if (shortestPath == null) {
            throw new FindPathException(ErrorType.NOT_EXIST_PATH);
        }
        return new Path(shortestPath.getVertexList(), (int) shortestPath.getWeight());
    }
}
