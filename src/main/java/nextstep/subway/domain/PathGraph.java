package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotConnectedPathException;
import nextstep.subway.domain.exception.NotEnoughStationsException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathGraph {

    public static final int MINIMUM_STATION_COUNT = 2;

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath dijkstraShortestPath;
    private final Lines lines;

    private PathGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Lines lines) {
        this.graph = graph;
        this.dijkstraShortestPath = new DijkstraShortestPath(graph);
        this.lines = lines;
    }

    public static PathGraph valueOf(Lines lines) {
        if (lines.hasLessThanStations(MINIMUM_STATION_COUNT)) {
            throw new NotEnoughStationsException();
        }
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        initializeVertex(graph, lines.getStations());
        initializeEdgeWeight(graph, lines.getSections());
        return new PathGraph(graph, lines);
    }

    private static void initializeVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Station> stations) {
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private static void initializeEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance().toInt());
        }
    }

    public Path findShortPath(Long source, Long target) {
        Station sourceStation = lines.findStation(source);
        Station targetStation = lines.findStation(target);
        if (sourceStation.equals(targetStation)) {
            throw new IllegalArgumentException();
        }

        GraphPath path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        if (path == null) {
            throw new NotConnectedPathException(sourceStation.getName(), targetStation.getName());
        }

        return new Path(path.getVertexList(), path.getWeight());
    }
}
